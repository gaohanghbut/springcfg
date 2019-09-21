package cn.yxffcode.springcfg.ctl.xml;

/**
 * @author gaohang
 */
public class TestBean {
  private String appName;

  private int runMode;

  private int env;

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public int getRunMode() {
    return runMode;
  }

  public void setRunMode(int runMode) {
    this.runMode = runMode;
  }

  public int getEnv() {
    return env;
  }

  public void setEnv(int env) {
    this.env = env;
  }

  public String getAppName() {
    return appName;
  }
}
