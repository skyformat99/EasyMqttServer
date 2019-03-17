package com.easyiot.iot.mqtt.server.plugin

import io.netty.channel.Channel
import io.netty.handler.codec.mqtt.MqttPublishMessage
import org.springframework.stereotype.Service

/**
 * 消息持久化插件
 * 实现 persistence 方法
 * 比如：保存再MySql，或者MongoDB，都可以在这里实现
 */
@Service
class MessagePersistencePluginImp implements MessagePersistencePlugin {
    @Override
    def persistence(Channel channel, MqttPublishMessage message) {
        println("channel:" + channel.id() + " message:" + message.fixedHeader().isRetain())

    }



    @Override
    def version() {
        return "0.0.1"
    }

    @Override
    def name() {
        return "Auth plugin"
    }

}
