package cn.yxffcode.springcfg.ctl.xml;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author gaohang
 */
public final class IfBeanDefinitionParser extends BeanParserSupport
    implements BeanDefinitionParser, BeanDefinitionDecorator {

  private final boolean reverse;
  private final boolean checkExists;

  public IfBeanDefinitionParser(final ConfigHolder configHolder, final boolean reverse,
                                final boolean checkExists) {
    super(configHolder);
    this.reverse = reverse;
    this.checkExists = checkExists;
  }

  @Override
  public BeanDefinition parse(final Element element, final ParserContext parserContext) {
    if (!checkCondition(element, reverse, checkExists)) {
      return null;
    }
    //取子元素
    final NodeList childNodes = element.getChildNodes();
    parseChildren(parserContext, childNodes);
    return null;
  }

  boolean checkCondition(final Element element, final boolean reverse, final boolean checkExists) {
    final String actual = getVariableValue(element.getAttribute("actual"));
    if (checkExists) {
      if (reverse) {
        return Strings.isNullOrEmpty(actual);
      }
      return !Strings.isNullOrEmpty(actual);
    }

    final String expect = element.getAttribute("expect");
    final boolean equals = StringUtils.equals(actual, expect);

    //反向，则相等时不满足条件，正向，则不相等时不满足条件，其它情况为满足条件
    if (reverse && equals) {
      return false;
    }
    if (!reverse && !equals) {
      return false;
    }
    return true;
  }

  private void parseChildren(final ParserContext parserContext, final NodeList childNodes) {
    for (int i = 0, j = childNodes.getLength(); i < j; i++) {
      final Node item = childNodes.item(i);
      if (item instanceof Element) {
        parseBean(parserContext, (Element) item);
      }
    }
  }

  @Override
  public BeanDefinitionHolder decorate(final Node node,
                                       final BeanDefinitionHolder beanDefinitionHolder,
                                       final ParserContext parserContext) {
    if (!checkCondition((Element) node, reverse, checkExists)) {
      return beanDefinitionHolder;
    }
    //decorate sub elements
    final NodeList childNodes = node.getChildNodes();
    for (int i = 0, j = childNodes.getLength(); i < j; i++) {
      final Node child = childNodes.item(i);
      if (!(child instanceof Element)) {
        continue;
      }
      if (parserContext.getDelegate().isDefaultNamespace(child)) {
        final String tagName = child.getLocalName();
        if ("property".equals(tagName)) {
          parserContext.getDelegate().parsePropertyElement((Element) child,
              beanDefinitionHolder.getBeanDefinition());
        } else {
          parserContext.getReaderContext().fatal("unsupported tag:" + child, node);
        }
      } else {
        return parserContext.getDelegate().decorateBeanDefinitionIfRequired((Element) child,
            beanDefinitionHolder);
      }
    }
    return beanDefinitionHolder;
  }
}
