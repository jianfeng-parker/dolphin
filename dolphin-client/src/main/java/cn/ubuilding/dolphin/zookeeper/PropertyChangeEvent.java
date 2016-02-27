package cn.ubuilding.dolphin.zookeeper;

/**
 * @author Wu Jianfeng
 * @since 16/2/26 07:41
 */

public class PropertyChangeEvent {

    /**
     * 发生变化的属性名称
     */
    private String propertyName;

    /**
     * 变化后的属性值
     */
    private Object propertyValue;

    /**
     * 属性变化类型定义
     */
    private ChangeType type;

    public enum ChangeType {
        /**
         * 属性对应的zookeeper节点已被删除
         */
        PROPERTY_REMOVED,

        /**
         * 属性对应的zookeeper节点被 创建或更新
         */
        PROPERTY_UPDATED
    }

    public PropertyChangeEvent(String propertyName, ChangeType type) {
        this.propertyName = propertyName;
        this.type = type;
    }

    public PropertyChangeEvent(String propertyName, Object propertyValue, ChangeType type) {
        this(propertyName, type);
        this.propertyValue = propertyValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

    public ChangeType getType() {
        return type;
    }
}
