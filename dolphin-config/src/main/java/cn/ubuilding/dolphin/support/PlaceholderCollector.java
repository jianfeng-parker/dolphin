package cn.ubuilding.dolphin.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 16/2/28 10:25
 */

public class PlaceholderCollector {

    private String placeholderPrefix;

    private String placeholderSuffix;

    private Map<String, List<BeanUsedHolder>> holderPropertiesMap = new HashMap<>();


    public PlaceholderCollector(String placeholderPrefix, String placeholderSuffix) {
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
    }

    /**
     * 将使用占位符的bean和对应的field缓存起来
     *
     * @param beanName    使用占位符的bean
     * @param fieldName   value为使用占位符的 field名称
     * @param placeholder 使用的占位符
     */
    public void collect(String beanName, String fieldName, String placeholder) {
        // 占位符名称,如: ${x} => x
        if (placeholder.startsWith(this.placeholderPrefix) && placeholder.endsWith(this.placeholderSuffix)) {
            placeholder = placeholder.substring(2);
            placeholder = placeholder.substring(0, placeholder.length() - 1);
            if (null == holderPropertiesMap.get(placeholder)) {
                holderPropertiesMap.put(placeholder, new ArrayList<BeanUsedHolder>());
            }
            holderPropertiesMap.get(placeholder).add(new BeanUsedHolder(beanName, fieldName));
        }
    }

    public List<BeanUsedHolder> getUsedPlaceholder(String placeholder) {
        return holderPropertiesMap.get(placeholder);
    }
}
