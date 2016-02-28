package cn.ubuilding.dolphin.properties.support;

/**
 * @author Wu Jianfeng
 * @since 16/2/26 07:41
 * 占位符对应的zookeeper节点变化事件
 */

public class PlaceholderChangeEvent {

    /**
     * 发生变化的节点名称
     */
    private String name;

    /**
     * 变化后的节点值
     */
    private Object value;

    /**
     * 变化类型定义
     */
    private ChangeType type;

    public enum ChangeType {
        /**
         * 节点已被删除
         */
        PROPERTY_REMOVED,

        /**
         * 属性对应的zookeeper节点被 创建或更新
         */
        PROPERTY_UPDATED
    }

    public PlaceholderChangeEvent(String name, ChangeType type) {
        this.name = name;
        this.type = type;
    }

    public PlaceholderChangeEvent(String name, Object value, ChangeType type) {
        this(name, type);
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public ChangeType getType() {
        return type;
    }
}
