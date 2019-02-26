/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.iot.mqtt.server.protocol;

import com.easyiot.iot.mqtt.server.common.client.IChannelStoreStoreService;
import com.easyiot.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.easyiot.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.easyiot.iot.mqtt.server.common.session.ISessionStoreService;
import com.easyiot.iot.mqtt.server.common.session.SessionStore;
import com.easyiot.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
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

    private IChannelStoreStoreService iChannelStoreStoreService;

    private ISessionStoreService iSessionStoreService;


    public DisConnect(ISessionStoreService sessionStoreService,
                      ISubscribeStoreService subscribeStoreService,
                      IDupPublishMessageStoreService dupPublishMessageStoreService,
                      IDupPubRelMessageStoreService dupPubRelMessageStoreService,
                      IChannelStoreStoreService iChannelStoreStoreService,
                      ISessionStoreService iSessionStoreService) {
        this.sessionStoreService = sessionStoreService;
        this.subscribeStoreService = subscribeStoreService;
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
        this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
        this.iChannelStoreStoreService = iChannelStoreStoreService;
        this.iSessionStoreService = iSessionStoreService;

    }


    public void processDisConnect(Channel channel, MqttMessage msg) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        SessionStore sessionStore = sessionStoreService.get(clientId);
        /**
         * 删除在线设备
         */
        String channelId = channel.id().asLongText();
        if (iChannelStoreStoreService.containsChannelId(channelId)) {
            System.out.println("设备异常掉线:" + iChannelStoreStoreService.getByChannelId(channelId));
            //删除Session
            iSessionStoreService.remove(iChannelStoreStoreService.getByChannelId(channelId).getClientId());
            //删除在线统计
            iChannelStoreStoreService.removeChannelId(channelId);
        }


        /**
         * 删除会话
         */
        if (sessionStore.isCleanSession()) {
            subscribeStoreService.removeForClient(clientId);
            dupPublishMessageStoreService.removeByClient(clientId);
            dupPubRelMessageStoreService.removeByClient(clientId);
        }
        LOGGER.debug("DISCONNECT - clientId: {}, cleanSession: {}", clientId, sessionStore.isCleanSession());
        sessionStoreService.remove(clientId);
        channel.close();
    }

}
