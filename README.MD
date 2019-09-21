# Springcfg
对Spring配置做增强

## 使用freemarker
使用方式：
```java
ClassPathFreeMarkerXmlApplicationContext ctx = new ClassPathFreeMarkerXmlApplicationContext("classpath:META-INF/app.properties", "classpath:META-INF/freemarkerApp.xml.ftl");
TestBean bean = ctx.getBean(TestBean.class);
```
freemarkerApp.xml.ftl为spring配置：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <#if env == "local">
        <bean class="cn.yxffcode.springcfg.ctl.xml.TestBean"/>
    </#if>
</beans>
```

app.properties中配置的是freemarkerApp.xml.ftl里的变量：
```properties
env=local
```

## 使用spring schema
### 添加ctl命名空间
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:ctl="http://www.yxffcode.cn/schema/ctl" xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd  http://www.yxffcode.cn/schema/ctl
            http://www.yxffcode.cn/schema/ctl/ctl.xsd">
</beans>
```
### 加载属性文件
此处的属性文件用于支持xml中的条件控制标签，也可作为普通的place-holder使用：
```xml
<ctl:place-holder location="classpath:/META-INF/app.properties"/>
```
### 使用条件标签
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:ctl="http://www.yxffcode.cn/schema/ctl" xmlns="http://www.springframework.org/schema/beans"
       xmlns:Util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd  http://www.yxffcode.cn/schema/ctl
            http://www.yxffcode.cn/schema/ctl/ctl.xsd http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd">

    <ctl:place-holder location="classpath:/META-INF/app.properties"/>
    
    <!--判断env是否等于local，如果是，则配置bean-->
    <ctl:if actual="${env}" expect="local">
        <bean class="cn.yxffcode.springcfg.ctl.xml.TestBean">
            <!--判断appName是否存在，如果存在则设置属性-->
            <ctl:exist actual="${appName}">
                <property name="appName" value="${appName}"/>
            </ctl:exist>
            <!--判断run_mode是否存在，不存在则配置属性-->
            <ctl:not-exist actual="${run_mode}">
                <property name="runMode" value="0"/>
            </ctl:not-exist>
        </bean>
    </ctl:if>

    <!--类似于switch语句 -->
    <ctl:switch variable="${env}">
        <ctl:case value="local">
            <bean class="cn.yxffcode.springcfg.ctl.xml.TestBean">
                <ctl:exist actual="${appName}">
                    <property name="appName" value="${appName}"/>
                </ctl:exist>
                <ctl:switch variable="${run_mode_v}">
                    <ctl:case value="1">
                        <property name="runMode" value="${run_mode_v}"/>
                    </ctl:case>
                    <ctl:default>
                        <property name="runMode" value="0"/>
                    </ctl:default>
                </ctl:switch>
            </bean>
        </ctl:case>
    </ctl:switch>

</beans>
```