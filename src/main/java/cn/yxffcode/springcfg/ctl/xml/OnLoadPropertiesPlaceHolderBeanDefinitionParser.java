package cn.yxffcode.springcfg.ctl.xml;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author gaohang
 */
public final class OnLoadPropertiesPlaceHolderBeanDefinitionParser implements BeanDefinitionParser {
  private static final Logger LOGGER = LoggerFactory
      .getLogger(OnLoadPropertiesPlaceHolderBeanDefinitionParser.class);
  private static final DefaultResourceLoader DEFAULT_RESOURCE_LOADER = new DefaultResourceLoader();

  private final ConfigHolder configHolder;

  private static final BeanNameGenerator BEAN_NAME_GENERATOR = new DefaultBeanNameGenerator();

  public OnLoadPropertiesPlaceHolderBeanDefinitionParser(final ConfigHolder configHolder) {
    this.configHolder = configHolder;
  }

  @Override
  public BeanDefinition parse(final Element element, final ParserContext parserContext) {
    final String location = element.getAttribute("location");
    if (StringUtils.isBlank(location)) {
      parserContext.getReaderContext().fatal("place-holder must has a config location", element);
    }
    loadConfig(element, parserContext, location);

    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
    beanDefinition.setBeanClass(CtlPlaceHolderProcessor.class);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(configHolder);
    parserContext.getRegistry().registerBeanDefinition(
        BEAN_NAME_GENERATOR.generateBeanName(beanDefinition, parserContext.getRegistry()), beanDefinition);

    return null;
  }

  private void loadConfig(final Element element, final ParserContext parserContext, final String location) {
    final Resource resource = DEFAULT_RESOURCE_LOADER.getResource(location);
    InputStream in = null;
    try {
      in = resource.getInputStream();
      final Properties properties = new Properties();
      properties.load(in);
      final Map<String, String> configs = Maps.newHashMap();
      for (Entry<Object, Object> en : properties.entrySet()) {
        configs.put((String) en.getKey(), (String) en.getValue());
      }
      configHolder.addConfigs(configs);
    } catch (IOException e) {
      parserContext.getReaderContext().fatal("place-holder load resource failed", element, e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          LOGGER.error("close place-holder failed: {}", element, e);
        }
      }
    }
  }

  public static final class CtlPlaceHolderProcessor extends PropertyPlaceholderConfigurer {
    private final ConfigHolder configHolder;

    public CtlPlaceHolderProcessor(final ConfigHolder configHolder) {
      this.configHolder = configHolder;
    }

    @Override
    protected String resolvePlaceholder(String placeholder, Properties props) {
      final String value = configHolder.getConfigs().get(placeholder);
      return value != null ? value : super.resolvePlaceholder(placeholder, props);
    }

  }
}
