#### dolphin-config :

  > * 用于代替Spring的PropertyPlaceholderConfigurer从zookeeper中动态获取属性值 

  > * 监听属性节点的变化以动态更新bean的属性值
  
  > * 这是一个jar，可通过Maven依赖,如:
  
      <dependency>
         <groupId>cn.ubuilding.dolphin</groupId>
         <artifactId>dolphin-config</artifactId>
         <version>1.0-SNAPSHOT</version>
      </dependency>
      
  > * 支持spring配置方式和@Value标签方式申明的属性占位符替换,如：
      
  1. Spring XML方式:
      
     ```xml
      
      <bean id="xx" class="xx.xx.A">
         <property name="name" value="${user.name}"/>
      </bean>
      
     ```
      
  2. @Value表示方式:
      
     ```java
      
      public class A{
      
        @Value("${user.name}")
        private String name;
        
        // setter and getter
      }
      
     ```
  注: 属性name的值为zookeeper节点: /xx/xx/.../user.name的值

      