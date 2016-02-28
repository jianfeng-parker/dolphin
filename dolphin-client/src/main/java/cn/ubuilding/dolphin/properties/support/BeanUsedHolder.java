package cn.ubuilding.dolphin.properties.support;


/**
 * @author Wu Jianfeng
 * @since 16/1/31 15:44
 */

public class BeanUsedHolder {

    private String beanName;

    private String fieldName;

    public BeanUsedHolder(String beanName, String fieldName) {
        this.beanName = beanName;
        this.fieldName = fieldName;
    }


    public String getBeanName() {
        return beanName;
    }

    public String getFieldName() {
        return fieldName;
    }

}
