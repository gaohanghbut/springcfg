package cn.yxffcode.springcfg.ctl.freemarker;

import cn.yxffcode.springcfg.ctl.xml.TestBean;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author gaohang
 */
public class ClassPathFreeMarkerXmlApplicationContextTest {

  @Test
  public void test() {
    ClassPathFreeMarkerXmlApplicationContext ctx = new ClassPathFreeMarkerXmlApplicationContext("classpath:META-INF/app.properties", "classpath:META-INF/freemarkerApp.xml.ftl");
    TestBean bean = ctx.getBean(TestBean.class);
    System.out.println("bean = " + bean);
  }
}