package com.easyiot.iot.mqtt.server.plugin.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * 插件基类
 */
public interface BasePlugin {
    Logger logger = LoggerFactory.getLogger(BasePlugin.class);

    UUID uuid = UUID.randomUUID();

    String version();

    String name();
}