package cn.ubuilding.dolphin.properties.config;

import cn.ubuilding.dolphin.properties.support.BeanUsedHolder;
import cn.ubuilding.dolphin.properties.support.DolphinPlaceholderCollector;
import cn.ubuilding.dolphin.properties.support.PlaceholderChangeEvent;
import cn.ubuilding.dolphin.properties.support.PlaceholderChanger;
import cn.ubuilding.dolphin.zookeeper.ZkContainer;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.*;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringValueResolver;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;


/**
 * @author Wu Jianfeng
 * @since 16/2/2 09:29
 */

public class DolphinPropertyPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer implements BeanPostProcessor {

    private MutablePropertySources propertySources;

    private String beanName;

    private BeanFactory beanFactory;

    private DolphinPlaceholderCollector placeholderCollector;

    public DolphinPropertyPlaceholderConfigurer() {
        if (null == this.placeholderCollector)
            this.placeholderCollector = new DolphinPlaceholderCollector(this.placeholderPrefix, this.placeholderSuffix);
    }

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
                placeholderCollector.collect(beanName, pv.getName(), _value);
            }
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, final String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                Value annotation = field.getAnnotation(Value.class);
                if (null != annotation) {
                    String aValue = annotation.value(); // 标签值 -> 占位符
                    placeholderCollector.collect(beanName, field.getName(), aValue);
                }
            }
        });
        return bean;
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
            zk.addChanger(new BeanPlaceholderChanger(this));
        }


        protected String getPropertyAsRawString(String key) {
            String value = zk.getProperty(key);
            return (null != value) ? value : getProperty(key, String.class, false);
        }

        public <T> T getProperty(String key, Class<T> targetValueType, boolean resolveNestedPlaceholders) {
            return super.getProperty(key, targetValueType, false);
        }
    }

    private class BeanPlaceholderChanger implements PlaceholderChanger {

        /**
         * 在获得property变更事件后动态更新bean中的对应属性值
         */
        private DolphinSourcesPropertyResolver resolver;

        public BeanPlaceholderChanger(DolphinSourcesPropertyResolver resolver) {
            this.resolver = resolver;
        }

        /**
         * @param event zookeeper节点发生变化的事件，如: /apps/app01/properties/x中的 "x"
         */
        @Override
        public void onChange(PlaceholderChangeEvent event) {
            List<BeanUsedHolder> list = DolphinPropertyPlaceholderConfigurer.this.placeholderCollector.getUsedPlaceholder(event.getName());
            if (null != list && list.size() > 0) {
                Object value = null;
                if (event.getType() == PlaceholderChangeEvent.ChangeType.PROPERTY_UPDATED) {
                    value = event.getValue();
                } else if (event.getType() == PlaceholderChangeEvent.ChangeType.PROPERTY_REMOVED) {
                    // 如果bean property对应的zookeeper节点被删除，则将property值更新为本地properties文件中的值
                    value = resolver.getProperty(event.getName(), String.class, false);
                }
                for (BeanUsedHolder holder : list) {
                    String beanName = holder.getBeanName();
                    Object bean = DolphinPropertyPlaceholderConfigurer.this.beanFactory.getBean(beanName);
                    if (null != bean) {
                        String filed = holder.getFieldName();
                        try {
                            BeanUtils.setProperty(bean, filed, value);
                        } catch (Exception e) {
                            logger.error("update value(" + event.getValue() + ") to property(" + filed + ") of bean(" + beanName + ") error:" + e.getMessage());
                        }
                    }
                }
            }
        }
    }

}
