package cn.ubuilding.dolphin.properties.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Wu Jianfeng
 * @since 16/1/31 10:08
 * 用于标记bean属性的值在发生改变可重新加载
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Zookeptable {
}
