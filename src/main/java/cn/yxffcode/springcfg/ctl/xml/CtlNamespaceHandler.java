package cn.yxffcode.springcfg.ctl.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author gaohang
 */
public class CtlNamespaceHandler extends NamespaceHandlerSupport {
  @Override
  public void init() {

    final ConfigHolder configHolder = new ConfigHolder();

    final IfBeanDefinitionParser ifBeanDefinitionParser = new IfBeanDefinitionParser(configHolder, false, false);
    registerBeanDefinitionParser("if", ifBeanDefinitionParser);
    registerBeanDefinitionDecorator("if", ifBeanDefinitionParser);

    final IfBeanDefinitionParser ifNotBeanDefinitionParser1 = new IfBeanDefinitionParser(configHolder, true, false);
    registerBeanDefinitionParser("if-not", ifNotBeanDefinitionParser1);
    registerBeanDefinitionDecorator("if-not", ifNotBeanDefinitionParser1);

    final IfBeanDefinitionParser ifExistsBeanDefinitionParser = new IfBeanDefinitionParser(configHolder, false, true);
    registerBeanDefinitionParser("exist", ifExistsBeanDefinitionParser);
    registerBeanDefinitionDecorator("exist", ifExistsBeanDefinitionParser);

    final IfBeanDefinitionParser ifNotExistsBeanDefinitionParser = new IfBeanDefinitionParser(configHolder, true, true);
    registerBeanDefinitionParser("not-exist", ifNotExistsBeanDefinitionParser);
    registerBeanDefinitionDecorator("not-exist", ifNotExistsBeanDefinitionParser);

    registerBeanDefinitionParser("place-holder", new OnLoadPropertiesPlaceHolderBeanDefinitionParser(configHolder));

    SwitchBeanDefinitionParser switchBeanDefinitionParser = new SwitchBeanDefinitionParser(configHolder);
    registerBeanDefinitionParser("switch", switchBeanDefinitionParser);
    registerBeanDefinitionDecorator("switch", switchBeanDefinitionParser);

  }
}
