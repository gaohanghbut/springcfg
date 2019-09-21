package cn.yxffcode.springcfg.ctl.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;

/**
 * @author gaohang
 */
public abstract class BeanParserSupport {
  private static final Logger LOGGER = LoggerFactory.getLogger(BeanParserSupport.class);
  protected final ConfigHolder configHolder;

  public BeanParserSupport(final ConfigHolder configHolder) {
    this.configHolder = configHolder;
  }

  protected void parseBean(final ParserContext parserContext, final Element element) {
    final BeanDefinitionParserDelegate delegate = parserContext.getDelegate();
    if (delegate.isDefaultNamespace(element)) {
      if ("import".equals(element.getLocalName())) {
        parseImport(element, element.getAttribute("resource"),
            parserContext.getReaderContext());
        return;
      }
      final BeanDefinitionHolder beanDefinitionHolder = delegate
          .parseBeanDefinitionElement(element);
      if (beanDefinitionHolder != null) {
        parserContext.getRegistry().registerBeanDefinition(
            beanDefinitionHolder.getBeanName(), beanDefinitionHolder.getBeanDefinition());
      }
    } else {
      delegate.parseCustomElement(element);
    }
  }

  protected String getVariableValue(final String variable) {
    if (variable.startsWith("${") && variable.endsWith("}")) {
      return configHolder.getConfigs().get(variable.substring(2, variable.length() - 1));
    }
    return variable;
  }

  private void parseImport(Element ele, String location, XmlReaderContext readerContext) {
    if (!StringUtils.hasText(location)) {
      readerContext.error("Resource location must not be empty", ele);
    } else {
      location = readerContext.getEnvironment().resolveRequiredPlaceholders(location);
      LinkedHashSet actualResources = new LinkedHashSet(4);
      boolean absoluteLocation = false;

      try {
        absoluteLocation = ResourcePatternUtils.isUrl(location) || ResourceUtils.toURI(location).isAbsolute();
      } catch (URISyntaxException e) {
      }

      int actResArray;
      if (absoluteLocation) {
        try {
          actResArray = readerContext.getReader().loadBeanDefinitions(location, actualResources);
          LOGGER.debug("Imported {} bean definitions from URL location [{}]", actResArray, location);
        } catch (BeanDefinitionStoreException e) {
          readerContext.error("Failed to import bean definitions from URL location [" + location + "]", ele, e);
        }
      } else {
        try {
          Resource relativeResource = readerContext.getResource().createRelative(location);
          if (relativeResource.exists()) {
            actResArray = readerContext.getReader().loadBeanDefinitions(relativeResource);
            actualResources.add(relativeResource);
          } else {
            String baseLocation = readerContext.getResource().getURL().toString();
            actResArray = readerContext.getReader().loadBeanDefinitions(StringUtils.applyRelativePath(baseLocation, location), actualResources);
          }

          LOGGER.debug("Imported {} bean definitions from relative location [{}]", actResArray, location);
        } catch (IOException e) {
          readerContext.error("Failed to resolve current resource location", ele, e);
        } catch (BeanDefinitionStoreException e) {
          readerContext.error("Failed to import bean definitions from relative location [" + location + "]", ele, e);
        }
      }

      Resource[] actResArray1 = (Resource[]) actualResources.toArray(new Resource[actualResources.size()]);
      readerContext.fireImportProcessed(location, actResArray1, readerContext.extractSource(ele));
    }
  }
}
