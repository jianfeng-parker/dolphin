## 这是一个zookeeper相关的项目（ubuilding-dolphin）：

------

目前主要包含两个子项目

------
dolphin-client :

  > * 在其它的项目中可用于代替Spring的PropertyPlaceholderConfigurer从zookeeper中读取properties配置. 

  > * 监听属性节点的变化以动态更新bean的属性值
  
  > * 这是一个jar，可通过Maven依赖,如:
  
      <dependency>
         <groupId>cn.ubuilding.dolphin</groupId>
         <artifactId>dolphin-client</artifactId>
         <version>1.0-SNAPSHOT</version>
      </dependency>
      
  > * 支持spring配置方式和@Value标签方式申明的属性占位符替换,如：
      
      <bean id="xx" class="xx.xx.A">
         <property name="name" value="${user.name}"/>
      </bean>
      
      public class A{
      
        @Value("${user.name}")
        private String name;
        
        // setter and getter
      }
      
      属性name的值为zookeeper节点: /xx/xx/.../user.name的值
      
dolphin-web :
  
  > * 基于zookeeper实现的统一配置中心应用
  
  > * 通过界面操作、查看zookeeper的节点
  
  > * dolphin-client中所监听的就是此web应用的zookeeper服务
  
  > * 暂未开发
      