package com.easyiot.iot.mqtt.server.plugin.auth;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息持久化插件
 * 实现 persistence 方法
 * 比如：保存再MySql，或者MongoDB，都可以在这里实现
 */
@Service
public class MessagePersistencePluginImp implements MessagePersistencePlugin {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void persistence(Channel channel, MqttPublishMessage message) {
        System.out.println("channel:" + channel.id() + " message:" + message.fixedHeader().isRetain());
        if (message.fixedHeader().isRetain()) {
            //保存 持久化 数据

        }

    }


    @Override
    public String version() {
        return "0.0.1";
    }

    @Override
    public String name() {
        return "Auth plugin";
    }

}
