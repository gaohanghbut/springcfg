package cn.yxffcode.springcfg.ctl.xml;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * @author gaohang
 */
public final class SwitchBeanDefinitionParser extends BeanParserSupport
    implements BeanDefinitionParser, BeanDefinitionDecorator {

  public SwitchBeanDefinitionParser(final ConfigHolder configHolder) {
    super(configHolder);
  }

  @Override
  public BeanDefinition parse(final Element element, final ParserContext parserContext) {
    final String variableValue = getVariableValue(element.getAttribute("variable"));
    //cases and default
    final List<Element> cases = getCases(element, "case");
    final boolean matched = processCases(parserContext, variableValue, cases);
    if (!matched) {
      final List<Element> defaultCase = getCases(element, "default");
      if (!defaultCase.isEmpty()) {
        parseChildren(parserContext, defaultCase.get(0).getChildNodes());
      }
    }

    return null;
  }

  private List<Element> getCases(final Element element, final String tagName) {
    List<Element> elements = Lists.newArrayList();
    final NodeList childNodes = element.getChildNodes();
    for (int i = 0, j = childNodes.getLength(); i < j; i++) {
      final Node item = childNodes.item(i);
      if (item instanceof Element) {
        final String tag = item.getLocalName();
        if (StringUtils.equals(tag, tagName)) {
          elements.add((Element) item);
        }
      }
    }
    return elements;
  }

  private boolean processCases(final ParserContext parserContext, final String variableValue,
                               final List<Element> cases) {
    for (int i = 0, j = cases.size(); i < j; i++) {
      final Element cs = (Element) cases.get(i);
      final String value = cs.getAttribute("value");
      if (StringUtils.equals(variableValue, value)) {
        parseChildren(parserContext, cs.getChildNodes());
        return true;
      }
    }
    return false;
  }

  private BeanDefinitionHolder processDecorateCases(final ParserContext parserContext, BeanDefinitionHolder definition, final String variableValue,
                                                    final List<Element> cases) {
    for (int i = 0, j = cases.size(); i < j; i++) {
      final Element cs = (Element) cases.get(i);
      final String value = cs.getAttribute("value");
      if (StringUtils.equals(variableValue, value)) {
        return parseDecorateChildren(parserContext, definition, cs.getChildNodes());
      }
    }
    return null;
  }

  private void parseChildren(final ParserContext parserContext, final NodeList childNodes) {
    for (int i = 0, j = childNodes.getLength(); i < j; i++) {
      final Node item = childNodes.item(i);
      if (item instanceof Element) {
        parseBean(parserContext, (Element) item);
      }
    }
  }

  private BeanDefinitionHolder parseDecorateChildren(final ParserContext parserContext, BeanDefinitionHolder definition, final NodeList childNodes) {
    for (int i = 0, j = childNodes.getLength(); i < j; i++) {
      final Node child = childNodes.item(i);
      if (!(child instanceof Element)) {
        continue;
      }
      if (parserContext.getDelegate().isDefaultNamespace(child)) {
        final String tagName = child.getLocalName();
        if ("property".equals(tagName)) {
          parserContext.getDelegate().parsePropertyElement((Element) child,
              definition.getBeanDefinition());
        } else {
          parserContext.getReaderContext().fatal("unsupported tag:" + child, child);
        }
      } else {
        return parserContext.getDelegate().decorateBeanDefinitionIfRequired((Element) child,
            definition);
      }
    }
    return definition;
  }

  @Override
  public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext) {
    final Element element = (Element) node;
    final String variableValue = getVariableValue(element.getAttribute("variable"));
    //cases and default
    final List<Element> cases = getCases(element, "case");
    BeanDefinitionHolder holder = processDecorateCases(parserContext, definition, variableValue, cases);
    if (holder == null) {
      final List<Element> defaultCase = getCases(element, "default");
      if (!defaultCase.isEmpty()) {
        holder = parseDecorateChildren(parserContext, definition, defaultCase.get(0).getChildNodes());
      }
    }

    return holder;
  }
}
