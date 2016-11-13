package cn.ubuilding.dolphin.web.pojo;

/**
 * @author Wu Jianfeng
 * @since 16/3/8 21:44
 */

public class ConfigInfo {

    private String name;

    private String value;

    private String mark;

    public ConfigInfo(String name, String value, String mark) {
        this.name = name;
        this.value = value;
        this.mark = mark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
