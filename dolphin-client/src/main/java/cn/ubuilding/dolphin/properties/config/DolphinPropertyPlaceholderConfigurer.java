package cn.ubuilding.dolphin.properties.config;

import cn.ubuilding.dolphin.properties.bean.BeanFieldHolder;
import cn.ubuilding.dolphin.zookeeper.PropertyChangeEvent;
import cn.ubuilding.dolphin.zookeeper.PropertyChanger;
import cn.ubuilding.dolphin.zookeeper.ZkContainer;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.*;
import org.springframework.util.Assert;
import org.springframework.util.StringValueResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Wu Jianfeng
 * @since 16/2/2 09:29
 */

public class DolphinPropertyPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {

    private MutablePropertySources propertySources;

    private String beanName;

    private BeanFactory beanFactory;

    private Map<String, List<BeanFieldHolder>> holderPropertiesMap = new HashMap<>();


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.propertySources == null) {
            this.propertySources = new MutablePropertySources();
            try {
                PropertySource<?> localPropertySource =
                        new PropertiesPropertySource(LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME, mergeProperties());
                if (this.localOverride) {
                    this.propertySources.addFirst(localPropertySource);
                } else {
                    this.propertySources.addLast(localPropertySource);
                }
            } catch (IOException ex) {
                throw new BeanInitializationException("Could not load properties", ex);
            }
        }

        processProperties(beanFactory, new DolphinSourcesPropertyResolver(this.propertySources));
    }


    public void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                    StringValueResolver valueResolver) {

        BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);

        String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
        for (String curName : beanNames) {
            // Check that we're not parsing our own bean definition,
            // to avoid failing on unresolvable placeholders in properties file locations.
            if (!(curName.equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
                BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(curName);
                try {
                    collectPropertyHolder(bd, curName);
                    visitor.visitBeanDefinition(bd);
                } catch (Exception ex) {
                    throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage(), ex);
                }
            }
        }

        // New in Spring 2.5: resolve placeholders in alias target names and aliases as well.
        beanFactoryToProcess.resolveAliases(valueResolver);

        // New in Spring 3.0: resolve placeholders in embedded values such as annotation attributes.
        beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
    }

    public PropertySources getAppliedPropertySources() throws IllegalStateException {
        Assert.state(this.propertySources != null, "PropertySources have not get been applied");
        return this.propertySources;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    private void collectPropertyHolder(BeanDefinition bd, String beanName) {
        MutablePropertyValues mpvs = bd.getPropertyValues();
        PropertyValue[] pvs = mpvs.getPropertyValues();
        for (PropertyValue pv : pvs) {
            Object value = pv.getValue();
            if (value instanceof TypedStringValue) {
                String _value = ((TypedStringValue) value).getValue();
                // 占位符名称,如: ${x} => x
                if (_value.startsWith(this.placeholderPrefix) && _value.endsWith(this.placeholderSuffix)) {
                    _value = _value.substring(2);
                    _value = _value.substring(0, _value.length() - 1);
                    if (null == holderPropertiesMap.get(_value)) {
                        holderPropertiesMap.put(_value, new ArrayList<BeanFieldHolder>());
                    }
                    holderPropertiesMap.get(_value).add(new BeanFieldHolder(beanName, pv.getName()));
                }
            }
        }
    }

    private class DolphinSourcesPropertyResolver extends PropertySourcesPropertyResolver {

        private ZkContainer zk = ZkContainer.getInstance();

        /**
         * Create a new resolver against the given property sources.
         *
         * @param propertySources the set of {@link PropertySource} objects to use
         */
        DolphinSourcesPropertyResolver(PropertySources propertySources) {
            super(propertySources);
            zk.addChanger(new BeanPropertyChanger(this));
        }


        protected String getPropertyAsRawString(String key) {
            String value = zk.getProperty(key);
            return (null != value) ? value : getProperty(key, String.class, false);
        }

        public <T> T getProperty(String key, Class<T> targetValueType, boolean resolveNestedPlaceholders) {
            return super.getProperty(key, targetValueType, false);
        }
    }

    private class BeanPropertyChanger implements PropertyChanger {

        /**
         * 在获得property变更事件后动态更新bean中的对应属性值
         */
        private DolphinSourcesPropertyResolver resolver;

        public BeanPropertyChanger(DolphinSourcesPropertyResolver resolver) {
            this.resolver = resolver;
        }

        /**
         * @param event zookeeper节点发生变化的事件，如: /apps/app01/properties/x中的 "x"
         */
        @Override
        public void onChange(PropertyChangeEvent event) {
            List<BeanFieldHolder> list = DolphinPropertyPlaceholderConfigurer.this.holderPropertiesMap.get(event.getPropertyName());
            if (null != list && list.size() > 0) {
                Object value = null;
                if (event.getType() == PropertyChangeEvent.ChangeType.PROPERTY_UPDATED) {
                    value = event.getPropertyValue();
                } else if (event.getType() == PropertyChangeEvent.ChangeType.PROPERTY_REMOVED) {
                    // 如果bean property对应的zookeeper节点被删除，则将property值更新为本地properties文件中的值
                    value = resolver.getProperty(event.getPropertyName(), String.class, false);
                }
                for (BeanFieldHolder holder : list) {
                    String beanName = holder.getBeanName();
                    Object bean = DolphinPropertyPlaceholderConfigurer.this.beanFactory.getBean(beanName);
                    if (null != bean) {
                        String filed = holder.getFieldName();
                        try {
                            BeanUtils.setProperty(bean, filed, value);
                        } catch (Exception e) {
                            logger.error("update value(" + event.getPropertyValue() + ") to property(" + filed + ") of bean(" + beanName + ") error:" + e.getMessage());
                        }
                    }
                }
            }
        }
    }

}
