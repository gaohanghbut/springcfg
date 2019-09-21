package cn.yxffcode.springcfg.ctl.freemarker;

import java.io.IOException;
import java.util.Map;

/**
 * @author gaohang
 */
public interface FreeMarkerApplicationContext {
  Map<String, Object> getContext() throws IOException;
}
