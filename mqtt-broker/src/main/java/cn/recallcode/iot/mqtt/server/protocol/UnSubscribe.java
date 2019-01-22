/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.recallcode.iot.mqtt.server.protocol;

import cn.recallcode.iot.mqtt.server.common.client.ITopicStoreService;
import cn.recallcode.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * UNSUBSCRIBE连接处理
 */
public class UnSubscribe {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnSubscribe.class);

    private ISubscribeStoreService iSubscribeStoreService;
    private ITopicStoreService iTopicStoreService;

    public UnSubscribe(ISubscribeStoreService subscribeStoreService, ITopicStoreService iTopicStoreService) {
        this.iSubscribeStoreService = subscribeStoreService;
        this.iTopicStoreService = iTopicStoreService;
    }

    public void processUnSubscribe(Channel channel, MqttUnsubscribeMessage msg) {
        List<String> topicFilters = msg.payload().topics();
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        topicFilters.forEach(topicFilter -> {
            iSubscribeStoreService.remove(topicFilter, clientId);
            LOGGER.debug("UNSUBSCRIBE - clientId: {}, topicFilter: {}", clientId, topicFilter);
        });
        /**
         *删除订阅的Topic缓存
         */
        iTopicStoreService.remove(channel.id().asLongText());
        MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()), null);
        channel.writeAndFlush(unsubAckMessage);
    }

}
