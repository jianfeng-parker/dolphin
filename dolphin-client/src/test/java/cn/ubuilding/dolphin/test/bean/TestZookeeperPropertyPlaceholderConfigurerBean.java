package cn.ubuilding.dolphin.test.bean;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author Wu Jianfeng
 * @since 16/2/1 08:24
 */

public class TestZookeeperPropertyPlaceholderConfigurerBean {

    private String x;

    private String y;

    private int z;

    @Value("${user.name}")
    private String name;

    @Value("30")
    private int age;

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
