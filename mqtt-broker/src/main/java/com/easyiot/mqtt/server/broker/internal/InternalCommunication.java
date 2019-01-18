/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.mqtt.server.broker.internal;

import cn.recallcode.iot.mqtt.server.common.message.IMessageIdService;
import cn.recallcode.iot.mqtt.server.common.session.ISessionStoreService;
import cn.recallcode.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import cn.recallcode.iot.mqtt.server.common.subscribe.SubscribeStore;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import org.apache.ignite.IgniteMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 内部通信, 基于发布-订阅范式
 */
@Component
public class InternalCommunication {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalCommunication.class);

    private final String internalTopic = "internal-communication-topic";

    @Autowired
    private IgniteMessaging igniteMessaging;

    @Autowired
    private ISessionStoreService sessionStoreService;

    @Autowired
    private ISubscribeStoreService subscribeStoreService;

    @Autowired
    private IMessageIdService messageIdService;

    @PostConstruct
    private void internalListen() {
        igniteMessaging.localListen(internalTopic, (nodeId, msg) -> {
            InternalMessage internalMessage = (InternalMessage) msg;
            this.sendPublishMessage(internalMessage.getTopic(), MqttQoS.valueOf(internalMessage.getMqttQoS()), internalMessage.getMessageBytes(), internalMessage.isRetain(), internalMessage.isDup());
            return true;
        });
    }

    public void internalSend(InternalMessage internalMessage) {
        if (igniteMessaging.clusterGroup().nodes() != null && igniteMessaging.clusterGroup().nodes().size() > 0) {
            igniteMessaging.send(internalTopic, internalMessage);
        }
    }

    private void sendPublishMessage(String topic, MqttQoS mqttQoS, byte[] messageBytes, boolean retain, boolean dup) {
        List<SubscribeStore> subscribeStores = subscribeStoreService.search(topic);
        subscribeStores.forEach(subscribeStore -> {
            if (sessionStoreService.containsKey(subscribeStore.getClientId())) {
                // 订阅者收到MQTT消息的QoS级别, 最终取决于发布消息的QoS和主题订阅的QoS
                sendByQOS(topic, mqttQoS, messageBytes, retain, dup, subscribeStore, LOGGER, sessionStoreService);
                //MqttQoS respQoS;

            }
        });
    }

    public void sendByQOS(String topic, MqttQoS mqttQoS, byte[] messageBytes, boolean retain, boolean dup, SubscribeStore subscribeStore, Logger logger, ISessionStoreService sessionStoreService) {
        MqttQoS respQoS = mqttQoS.value() > subscribeStore.getMqttQoS() ? MqttQoS.valueOf(subscribeStore.getMqttQoS()) : mqttQoS;
        //QOS 0 没有MessageID
        if (respQoS == MqttQoS.AT_MOST_ONCE) {
            MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                    new MqttPublishVariableHeader(topic, 0), Unpooled.buffer().writeBytes(messageBytes));
            logger.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}", subscribeStore.getClientId(), topic, respQoS.value());
            sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
        }
        //QOS 1

        if (respQoS == MqttQoS.AT_LEAST_ONCE) {

            handQOS1AndQOS2(topic, messageBytes, retain, dup, subscribeStore, sessionStoreService, respQoS);

        }
        //QOS 2
        if (respQoS == MqttQoS.EXACTLY_ONCE) {
            handQOS1AndQOS2(topic, messageBytes, retain, dup, subscribeStore, sessionStoreService, respQoS);
        }
    }

    /**
     * QOS 1 2比较特殊 消息必须带有MessageID，而且要缓存进内存，所以单独提取一个方法
     *
     * @param topic
     * @param messageBytes
     * @param retain
     * @param dup
     * @param subscribeStore
     * @param sessionStoreService
     * @param respQoS
     */

    private void handQOS1AndQOS2(String topic, byte[] messageBytes, boolean retain, boolean dup, SubscribeStore subscribeStore, ISessionStoreService sessionStoreService, MqttQoS respQoS) {
        int messageId = messageIdService.getNextMessageId();
        MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
        LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);

        sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
    }

}
