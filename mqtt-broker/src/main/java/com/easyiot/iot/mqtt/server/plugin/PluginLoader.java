package com.easyiot.iot.mqtt.server.plugin;


import com.easyiot.iot.mqtt.server.plugin.auth.AuthPluginImp;
import com.easyiot.iot.mqtt.server.plugin.auth.BasePlugin;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件加载器
 */
@Component
public class PluginLoader {
    private final List<Object> pluginList = new ArrayList<>();

    void installPlugin(BasePlugin basePlugin) {
        pluginList.add("");

    }

    List<Object> pluginList() {
        return pluginList;
    }

    PluginLoader() {
        this.installPlugin(new AuthPluginImp());

    }
}
