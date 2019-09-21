package cn.yxffcode.springcfg.ctl.freemarker;

import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @author gaohang
 */
public class ClassPathFreeMarkerXmlApplicationContext extends ClassPathXmlApplicationContext implements FreeMarkerApplicationContext {

  private final String appProperties;
  private final Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
  /**
   * freemarker rend environment
   */
  private Map<String, Object> context;

  public ClassPathFreeMarkerXmlApplicationContext(String appProperties) {
    this.appProperties = appProperties;
  }

  public ClassPathFreeMarkerXmlApplicationContext(String appProperties, ApplicationContext parent) {
    this(appProperties, new String[]{}, true, parent);
  }

  public ClassPathFreeMarkerXmlApplicationContext(String appProperties, String configLocation) throws BeansException {
    this(appProperties, new String[]{configLocation}, true, null);

  }

  public ClassPathFreeMarkerXmlApplicationContext(String appProperties, String... configLocations) throws BeansException {
    this(appProperties, configLocations, true, null);

  }

  public ClassPathFreeMarkerXmlApplicationContext(String appProperties, String[] configLocations, ApplicationContext parent) throws BeansException {
    this(appProperties, configLocations, true, parent);

  }

  public ClassPathFreeMarkerXmlApplicationContext(String appProperties, String[] configLocations, boolean refresh) throws BeansException {
    this(appProperties, configLocations, refresh, null);

  }

  public ClassPathFreeMarkerXmlApplicationContext(String appProperties, String[] configLocations, boolean refresh, ApplicationContext parent) throws BeansException {
    super(configLocations, false, parent);
    this.appProperties = appProperties;
    refresh();
  }

  @Override
  public Map<String, Object> getContext() throws IOException {
    if (context == null) {
      ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(getClassLoader());
      Resource[] resources = resourcePatternResolver.getResources(appProperties);

      Map<String, Object> ctx = Maps.newHashMap();
      for (Resource resource : resources) {
        try (InputStream in = resource.getInputStream()) {
          Properties properties = new Properties();
          properties.load(in);

          for (Map.Entry<Object, Object> en : properties.entrySet()) {
            ctx.put((String) en.getKey(), en.getValue());
          }
        }
      }
      this.context = ctx;
    }
    return context;
  }

  @Override
  public Resource getResource(String location) {
    Resource resource = super.getResource(location);
    try {
      return new FreeMarkerResource(resource, configuration, getContext());
    } catch (IOException e) {
      throw new FreeMarkResourceReadException(location + " load as freemarker resource failed", e);
    }
  }

}
