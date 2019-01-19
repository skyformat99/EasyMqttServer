/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.mqtt.server.broker.protocol;

import cn.recallcode.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * UNSUBSCRIBE连接处理
 */
public class UnSubscribe {

	private static final Logger LOGGER = LoggerFactory.getLogger(UnSubscribe.class);

	private ISubscribeStoreService subscribeStoreService;

	public UnSubscribe(ISubscribeStoreService subscribeStoreService) {
		this.subscribeStoreService = subscribeStoreService;
	}

	public void processUnSubscribe(Channel channel, MqttUnsubscribeMessage msg) {
		List<String> topicFilters = msg.payload().topics();
		String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
		topicFilters.forEach(topicFilter -> {
			subscribeStoreService.remove(topicFilter, clientId);
			LOGGER.debug("UNSUBSCRIBE - clientId: {}, topicFilter: {}", clientId, topicFilter);
		});
		MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()), null);
		channel.writeAndFlush(unsubAckMessage);
	}

}