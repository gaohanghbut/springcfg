package cn.yxffcode.springcfg.ctl.xml;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author gaohang
 */
public final class ConfigHolder {
    private final Map<String, String> configs = Maps.newHashMap();

    /**
     * Getter for property 'configs'.
     *
     * @return Value for property 'configs'.
     */
    public Map<String, String> getConfigs() {
        return configs;
    }

    /**
     * Setter for property 'configs'.
     *
     * @param configs Value to set for property 'configs'.
     */
    public void addConfigs(final Map<String, String> configs) {
        this.configs.putAll(configs);
    }
}
