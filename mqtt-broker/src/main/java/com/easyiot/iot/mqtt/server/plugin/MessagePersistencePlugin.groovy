package com.easyiot.iot.mqtt.server.plugin

import io.netty.channel.Channel
import io.netty.handler.codec.mqtt.MqttPublishMessage

/**
 * 消息持久化插件
 */
interface MessagePersistencePlugin extends BasePlugin {
    def persistence(Channel channel, MqttPublishMessage message)
}
