/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.recallcode.iot.mqtt.server.protocol;

import cn.hutool.core.util.StrUtil;
import cn.recallcode.iot.mqtt.server.common.message.IMessageIdService;
import cn.recallcode.iot.mqtt.server.common.message.IRetainMessageStoreService;
import cn.recallcode.iot.mqtt.server.common.message.RetainMessageStore;
import cn.recallcode.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import cn.recallcode.iot.mqtt.server.common.subscribe.SubscribeStore;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * SUBSCRIBE连接处理
 */
public class Subscribe {

    private static final Logger LOGGER = LoggerFactory.getLogger(Subscribe.class);

    private ISubscribeStoreService subscribeStoreService;

    private IMessageIdService messageIdService;

    private IRetainMessageStoreService retainMessageStoreService;

    public Subscribe(ISubscribeStoreService subscribeStoreService, IMessageIdService messageIdService, IRetainMessageStoreService retainMessageStoreService) {
        this.subscribeStoreService = subscribeStoreService;
        this.messageIdService = messageIdService;
        this.retainMessageStoreService = retainMessageStoreService;
    }

    public void processSubscribe(Channel channel, MqttSubscribeMessage msg) {
        List<MqttTopicSubscription> topicSubscriptions = msg.payload().topicSubscriptions();
        if (this.validTopicFilter(topicSubscriptions)) {
            String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
            List<Integer> mqttQoSList = new ArrayList<Integer>();
            topicSubscriptions.forEach(topicSubscription -> {
                String topicFilter = topicSubscription.topicName();
                MqttQoS mqttQoS = topicSubscription.qualityOfService();
                SubscribeStore subscribeStore = new SubscribeStore(clientId, topicFilter, mqttQoS.value());
                subscribeStoreService.put(topicFilter, subscribeStore);
                mqttQoSList.add(mqttQoS.value());
                LOGGER.debug("SUBSCRIBE - clientId: {}, topFilter: {}, QoS: {}", clientId, topicFilter, mqttQoS.value());
            });
            /**
             * SUBACK报文
             */
            MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()),
                    new MqttSubAckPayload(mqttQoSList));
            channel.writeAndFlush(subAckMessage);
            /**
             *发送保留消息
             */
            topicSubscriptions.forEach(topicSubscription -> {
                String topicFilter = topicSubscription.topicName();
                MqttQoS mqttQoS = topicSubscription.qualityOfService();
                this.sendRetainMessage(channel, topicFilter, mqttQoS);
            });
        } else {
            channel.close();
        }
    }

    /**
     * 验证topic合法性
     * <p>
     * 注意：一切topic顶上必须以 / 开头
     *
     * @param topicSubscriptions
     * @return
     */

    private boolean validTopicFilter(List<MqttTopicSubscription> topicSubscriptions) {
        for (MqttTopicSubscription topicSubscription : topicSubscriptions) {
            String topicFilter = topicSubscription.topicName();
            /**
             * 以#或+符号开头的、以/符号结尾的及不存在/符号的订阅按非法订阅处理, 这里没有参考标准协议,
             */
            if (StrUtil.startWith(topicFilter, '#') || StrUtil.startWith(topicFilter, '+') || StrUtil.endWith(topicFilter, '/') || !StrUtil.contains(topicFilter, '/'))
                return false;
            if (StrUtil.contains(topicFilter, '#')) {
                /**
                 * 不是以/#字符串结尾的订阅按非法订阅处理
                 */
                if (!StrUtil.endWith(topicFilter, "/#")) return false;
                /**
                 * 如果出现多个#符号的订阅按非法订阅处理
                 */
                if (StrUtil.count(topicFilter, '#') > 1) return false;
            }
            if (StrUtil.contains(topicFilter, '+')) {
                /**
                 *如果+符号和/+字符串出现的次数不等的情况按非法订阅处理
                 */
                if (StrUtil.count(topicFilter, '+') != StrUtil.count(topicFilter, "/+")) return false;
            }
        }
        return true;
    }


    /**
     * 客户端刚上线就给他发送PUB 的最新消息,这个消息成为"保留消息"
     * 如果PUBLISH消息的RETAIN标记位被设置为1，则称该消息为“保留消息”；
     * Broker会存储每个Topic的最后一条保留消息及其Qos，当订阅该Topic的客户端上线后，Broker需要将该消息投递给它。
     * 可以让新订阅的客户端得到发布方的最新的状态值，而不必要等待发送。
     * <p>
     * A retained message makes sense, when newly connected subscribers should receive messages immediately and shouldn’t have to wait until a publishing client sends the next message. This is extremely helpful when for status updates of components or devices on individual topics. For example the status of device1 is on the topic myhome/devices/device1/status, a new subscriber to the topic will get the status (online/offline) of the device immediately after subscribing when retained messages are used. The same is true for clients, which send data in intervals, temperature, GPS coordinates and other data. Without retained messages new subscribers are kept in the dark between publish intervals. So using retained messages helps to provide the last good value to a connecting client immediately.
     *
     * @param channel
     * @param topicFilter
     * @param mqttQoS
     */
    private void sendRetainMessage(Channel channel, String topicFilter, MqttQoS mqttQoS) {
        /**
         * 在缓存中找有没有保留消息
         *
         */
        List<RetainMessageStore> retainMessageStores = retainMessageStoreService.search(topicFilter);
        retainMessageStores.forEach(retainMessageStore -> {
            /**
             * QOS 0 AT_MOST_ONCE
             */
            MqttQoS respQoS = retainMessageStore.getMqttQoS() > mqttQoS.value() ? mqttQoS : MqttQoS.valueOf(retainMessageStore.getMqttQoS());
            if (respQoS == MqttQoS.AT_MOST_ONCE) {
                MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PUBLISH, false, respQoS, false, 0),
                        new MqttPublishVariableHeader(retainMessageStore.getTopic(), 0), Unpooled.buffer().writeBytes(retainMessageStore.getMessageBytes()));
                LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}", channel.attr(AttributeKey.valueOf("clientId")).get(), retainMessageStore.getTopic(), respQoS.value());
                channel.writeAndFlush(publishMessage);
            }
            /**
             * QOS 1 AT_LEAST_ONCE
             */
            if (respQoS == MqttQoS.AT_LEAST_ONCE) {
                handRetainMessage(channel, retainMessageStore, respQoS);
            }
            /**
             * QOS 2 EXACTLY_ONCE
             */
            if (respQoS == MqttQoS.EXACTLY_ONCE) {
                handRetainMessage(channel, retainMessageStore, respQoS);
            }
        });
    }

    /**
     * 专门处理 Retain Message
     * 为什么只处理 QOS1 好2 而0 单独处理？
     * 因为在QOS0 下，是没有MessageId的
     * 只有QOS1||2下才有MessageID
     *
     * @param channel
     * @param retainMessageStore
     * @param respQoS
     */

    private void handRetainMessage(Channel channel, RetainMessageStore retainMessageStore, MqttQoS respQoS) {
        int messageId = messageIdService.getNextMessageId();
        MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH, false, respQoS, false, 0),
                new MqttPublishVariableHeader(retainMessageStore.getTopic(), messageId), Unpooled.buffer().writeBytes(retainMessageStore.getMessageBytes()));
        LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", channel.attr(AttributeKey.valueOf("clientId")).get(), retainMessageStore.getTopic(), respQoS.value(), messageId);
        channel.writeAndFlush(publishMessage);
    }

}
