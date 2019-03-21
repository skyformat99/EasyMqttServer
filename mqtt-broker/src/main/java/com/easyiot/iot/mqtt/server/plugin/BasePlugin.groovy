package com.easyiot.iot.mqtt.server.plugin

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 插件基类
 */
interface BasePlugin {
    Logger logger = LoggerFactory.getLogger(BasePlugin.class)

    UUID uuid = UUID.randomUUID()

    abstract def version()

    abstract def name()
}