package cn.yxffcode.springcfg.ctl.freemarker;

import com.google.common.io.ByteStreams;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.Map;

/**
 * @author gaohang
 */
public class FreeMarkerResource extends AbstractResource {

  private final Resource target;
  private final Configuration configuration;
  private final Map<String, Object> context;

  public FreeMarkerResource(Resource target, Configuration configuration, Map<String, Object> context) {
    this.target = target;
    this.configuration = configuration;
    this.context = context;
  }

  @Override
  public String getDescription() {
    return target.getDescription();
  }

  @Override
  public InputStream getInputStream() throws IOException {
    InputStream inputStream = target.getInputStream();
    Template template = new Template(target.getFilename(), new InputStreamReader(inputStream), configuration);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try (OutputStreamWriter out = new OutputStreamWriter(bos)) {
      template.process(context, out);
      return new ByteArrayInputStream(bos.toByteArray());
    } catch (TemplateException e) {
      throw new IOException("process freemarker failed, current resource is " + target.getDescription(), e);
    }
  }
}
