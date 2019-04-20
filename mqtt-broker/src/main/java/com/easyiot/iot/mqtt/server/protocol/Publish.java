/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.iot.mqtt.server.protocol;

import com.easyiot.iot.mqtt.server.common.message.*;
import com.easyiot.iot.mqtt.server.common.session.ISessionStoreService;
import com.easyiot.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import com.easyiot.iot.mqtt.server.common.subscribe.SubscribeStore;
import com.easyiot.iot.mqtt.server.internal.InternalCommunication;
import com.easyiot.iot.mqtt.server.internal.InternalMessage;
import com.easyiot.iot.mqtt.server.plugin.auth.MessagePersistencePlugin;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * PUBLISH连接处理
 */
public class Publish {

    private static final Logger LOGGER = LoggerFactory.getLogger(Publish.class);

    private ISessionStoreService sessionStoreService;

    private ISubscribeStoreService subscribeStoreService;

    private IMessageIdService messageIdService;

    private IRetainMessageStoreService retainMessageStoreService;

    private IDupPublishMessageStoreService dupPublishMessageStoreService;

    private InternalCommunication internalCommunication;

    private MessagePersistencePlugin messagePersistencePlugin;

    public Publish(ISessionStoreService sessionStoreService,
                   ISubscribeStoreService subscribeStoreService,
                   IMessageIdService messageIdService,
                   IRetainMessageStoreService retainMessageStoreService,
                   IDupPublishMessageStoreService dupPublishMessageStoreService,
                   InternalCommunication internalCommunication,
                   MessagePersistencePlugin messagePersistencePlugin) {
        this.sessionStoreService = sessionStoreService;
        this.subscribeStoreService = subscribeStoreService;
        this.messageIdService = messageIdService;
        this.retainMessageStoreService = retainMessageStoreService;
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
        this.internalCommunication = internalCommunication;
        this.messagePersistencePlugin = messagePersistencePlugin;
    }

    /**
     * 处理发布消息事件
     *
     * @param channel
     * @param msg
     */

    public void processPublish(Channel channel, MqttPublishMessage msg) {
        //System.out.println("publish:" + msg.fixedHeader().isRetain());
        messagePersistencePlugin.persistence(channel, msg);
        //
        /**
         * QoS=0
         */
        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_MOST_ONCE) {
            genMqttMessage(msg);
        }
        /**
         * QoS=1
         */
        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
            genMqttMessage(msg);
            this.sendPubAckMessage(channel, msg.variableHeader().packetId());
        }
        /**
         * QoS=2
         */
        if (msg.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE) {
            genMqttMessage(msg);
            this.sendPubRecMessage(channel, msg.variableHeader().packetId());
        }

        /**
         * retain=1, 保留消息
         */
        if (msg.fixedHeader().isRetain()) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            if (messageBytes.length == 0) {
                retainMessageStoreService.remove(msg.variableHeader().topicName());
            } else {
                RetainMessageStore retainMessageStore = new RetainMessageStore().setTopic(msg.variableHeader().topicName()).setMqttQoS(msg.fixedHeader().qosLevel().value())
                        .setMessageBytes(messageBytes);
                retainMessageStoreService.put(msg.variableHeader().topicName(), retainMessageStore);
            }
        }
    }

    private void genMqttMessage(MqttPublishMessage msg) {
        byte[] messageBytes = new byte[msg.payload().readableBytes()];
        msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
        InternalMessage internalMessage = new InternalMessage().setTopic(msg.variableHeader().topicName())
                .setMqttQoS(msg.fixedHeader().qosLevel().value()).setMessageBytes(messageBytes)
                .setDup(false).setRetain(false);
        internalCommunication.internalSend(internalMessage);
        this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
    }

    /**
     * 发布消息
     *
     * @param topic
     * @param mqttQoS
     * @param messageBytes
     * @param retain
     * @param dup
     */
    private void sendPublishMessage(String topic, MqttQoS mqttQoS, byte[] messageBytes, boolean retain, boolean dup) {
        try {
            LOGGER.info("PUBLISH -  topic: {}, Qos: {}, message: {} ,retain{} , dup{}", topic, mqttQoS, new String(messageBytes, "utf-8"), retain, dup);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Unsupported encoding!");
        }
        List<SubscribeStore> subscribeStores = subscribeStoreService.search(topic);
        subscribeStores.forEach(subscribeStore -> {
            if (sessionStoreService.containsKey(subscribeStore.getClientId())) {
                // 订阅者收到MQTT消息的QoS级别, 最终取决于发布消息的QoS和主题订阅的QoS
                /**
                 * 0
                 */
                MqttQoS respQoS = mqttQoS.value() > subscribeStore.getMqttQoS() ? MqttQoS.valueOf(subscribeStore.getMqttQoS()) : mqttQoS;
                if (respQoS == MqttQoS.AT_MOST_ONCE) {
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, 0), Unpooled.buffer().writeBytes(messageBytes));
                    LOGGER.info("PUBLISH - clientId: {}, topic: {}, Qos: {}", subscribeStore.getClientId(), topic, respQoS.value());
                    sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
                }
                /**
                 * 1
                 */
                if (respQoS == MqttQoS.AT_LEAST_ONCE) {
                    genMqttMessage(topic, messageBytes, retain, dup, subscribeStore, respQoS);
                }
                /**
                 * 2
                 */
                if (respQoS == MqttQoS.EXACTLY_ONCE) {
                    genMqttMessage(topic, messageBytes, retain, dup, subscribeStore, respQoS);
                }
            }
        });
    }

    private void genMqttMessage(String topic, byte[] messageBytes, boolean retain, boolean dup, SubscribeStore subscribeStore, MqttQoS respQoS) {
        int messageId = messageIdService.getNextMessageId();
        MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
        LOGGER.info("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
        DupPublishMessageStore dupPublishMessageStore = new DupPublishMessageStore().setClientId(subscribeStore.getClientId())
                .setTopic(topic).setMqttQoS(respQoS.value()).setMessageBytes(messageBytes);
        dupPublishMessageStoreService.put(subscribeStore.getClientId(), dupPublishMessageStore);
        sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
    }

    /**
     * 发布 ·发布ACK报文·
     *
     * @param channel
     * @param messageId
     */
    private void sendPubAckMessage(Channel channel, int messageId) {
        MqttPubAckMessage pubAckMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
        channel.writeAndFlush(pubAckMessage);
    }

    /**
     * Rec报文
     *
     * @param channel
     * @param messageId
     */

    private void sendPubRecMessage(Channel channel, int messageId) {
        MqttMessage pubRecMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
        channel.writeAndFlush(pubRecMessage);
    }

}