/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.iot.mqtt.server.protocol;

import com.alibaba.fastjson.JSON;
import com.easyiot.iot.mqtt.server.common.client.IChannelStoreService;
import com.easyiot.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.easyiot.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.easyiot.iot.mqtt.server.common.session.ISessionStoreService;
import com.easyiot.iot.mqtt.server.common.session.SessionStore;
import com.easyiot.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * DISCONNECT连接处理
 */
public class DisConnect {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisConnect.class);

    private ISessionStoreService sessionStoreService;

    private ISubscribeStoreService subscribeStoreService;

    private IDupPublishMessageStoreService dupPublishMessageStoreService;

    private IDupPubRelMessageStoreService dupPubRelMessageStoreService;

    private IChannelStoreService iChannelStoreService;

    private ISessionStoreService iSessionStoreService;


    public DisConnect(ISessionStoreService sessionStoreService,
                      ISubscribeStoreService subscribeStoreService,
                      IDupPublishMessageStoreService dupPublishMessageStoreService,
                      IDupPubRelMessageStoreService dupPubRelMessageStoreService,
                      IChannelStoreService iChannelStoreService,
                      ISessionStoreService iSessionStoreService) {
        this.sessionStoreService = sessionStoreService;
        this.subscribeStoreService = subscribeStoreService;
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
        this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
        this.iChannelStoreService = iChannelStoreService;
        this.iSessionStoreService = iSessionStoreService;

    }


    public void processDisConnect(Channel channel, MqttMessage msg) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        SessionStore sessionStore = sessionStoreService.get(clientId);
        /**
         * 删除在线设备
         */
        String channelId = channel.id().asLongText();
        if (iChannelStoreService.containsChannelId(channelId)) {
            LOGGER.info("设备异常掉线:" + iChannelStoreService.getByChannelId(channelId));
            //删除Session
            iSessionStoreService.remove(iChannelStoreService.getByChannelId(channelId).getClientId());
            //删除在线统计
            iChannelStoreService.removeChannelId(channelId);
        }
        /**
         *  然后开始 给特殊通道发送客户端掉线的消息
         *   MqttFixedHeader(MqttMessageType messageType, boolean isDup, MqttQoS qosLevel, boolean isRetain, int remainingLength)
         */

        MqttPublishMessage pubAckMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_LEAST_ONCE, false, 0),
                new MqttPublishVariableHeader("/$SYS/CLIENT/DISCONNECT", 1), Unpooled.buffer().writeBytes(JSON.toJSONString(iChannelStoreService.getByChannelId(channelId)).getBytes()));
        channel.writeAndFlush(pubAckMessage);
        iChannelStoreService.removeChannelId(channelId);


        /**
         * 删除会话
         */
        if (sessionStore.isCleanSession()) {
            subscribeStoreService.removeForClient(clientId);
            dupPublishMessageStoreService.removeByClient(clientId);
            dupPubRelMessageStoreService.removeByClient(clientId);
        }
        LOGGER.info("DISCONNECT - clientId: {}, cleanSession: {}", clientId, sessionStore.isCleanSession());
        sessionStoreService.remove(clientId);
        channel.close();
    }

}
