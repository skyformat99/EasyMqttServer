package com.easyiot.iot.mqtt.server.plugin


import org.springframework.stereotype.Component

/**
 * 插件加载器
 */
@Component
class PluginLoader {
    private final List<Object> pluginMap = new ArrayList<>()

    def installPlugin(BasePlugin basePlugin) {
        pluginMap.add([uuid: basePlugin.uuid, name: basePlugin.name(), version: basePlugin.version()])

    }

    def pluginList() {
        return pluginMap
    }

    PluginLoader() {
        this.installPlugin(new AuthPluginImp())

    }
}
