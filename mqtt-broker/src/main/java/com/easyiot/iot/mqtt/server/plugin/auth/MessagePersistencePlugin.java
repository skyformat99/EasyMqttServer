package com.easyiot.iot.mqtt.server.plugin.auth;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 * 消息持久化插件
 */
public interface MessagePersistencePlugin extends BasePlugin {
    void persistence(Channel channel, MqttPublishMessage message);
}
