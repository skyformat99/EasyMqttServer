/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.iot.mqtt.server.protocol;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.easyiot.iot.mqtt.server.common.auth.IAuthService;
import com.easyiot.iot.mqtt.server.common.client.ChannelStore;
import com.easyiot.iot.mqtt.server.common.client.IChannelStoreService;
import com.easyiot.iot.mqtt.server.common.message.DupPubRelMessageStore;
import com.easyiot.iot.mqtt.server.common.message.DupPublishMessageStore;
import com.easyiot.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.easyiot.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.easyiot.iot.mqtt.server.common.session.ISessionStoreService;
import com.easyiot.iot.mqtt.server.common.session.SessionStore;
import com.easyiot.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import com.easyiot.iot.mqtt.server.plugin.AuthPlugin;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * CONNECT连接处理
 */
public class Connect {

    private static final Logger LOGGER = LoggerFactory.getLogger(Connect.class);

    private ISessionStoreService iSessionStoreService;

    private ISubscribeStoreService subscribeStoreService;

    private IDupPublishMessageStoreService dupPublishMessageStoreService;

    private IDupPubRelMessageStoreService dupPubRelMessageStoreService;

    private IAuthService authService;

    private AuthPlugin authPlugin;

    private IChannelStoreService iChannelStoreService;


    public Connect(IChannelStoreService iChannelStoreService,
                   ISessionStoreService sessionStoreService,
                   ISubscribeStoreService subscribeStoreService,
                   IDupPublishMessageStoreService dupPublishMessageStoreService,
                   IDupPubRelMessageStoreService dupPubRelMessageStoreService,
                   IAuthService authService, AuthPlugin authPlugin) {
        this.iSessionStoreService = sessionStoreService;
        this.subscribeStoreService = subscribeStoreService;
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
        this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
        this.authService = authService;
        this.iChannelStoreService = iChannelStoreService;
        this.authPlugin = authPlugin;
    }

    public void processConnect(Channel channel, MqttConnectMessage msg) {
        // 消息解码器出现异常
        if (msg.decoderResult().isFailure()) {
            LOGGER.info("DISCONNECT 消息解码器出现异常 - clientId: {}, cleanSession: {}", msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());

            Throwable cause = msg.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                // 不支持的协议版本
                MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false), null);
                channel.writeAndFlush(connAckMessage);
                channel.close();
                return;
            } else if (cause instanceof MqttIdentifierRejectedException) {
                // 不合格的clientId
                closeChannel(channel);
            }
            channel.close();
            return;
        }

        /**
         * clientId为空或null的情况, 这里要求客户端必须提供clientId, 不管cleanSession是否为1, 此处没有参考标准协议实现
         */
        if (StrUtil.isBlank(msg.payload().clientIdentifier())) {
            LOGGER.info("DISCONNECT 必须有ClientID - clientId: {}, cleanSession: {}", msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());

            closeChannel(channel);
            return;
        }
        /**
         * 用户名和密码认证
         *
         */
        // 用户名和密码验证, 这里要求客户端连接时必须提供用户名和密码, 不管是否设置用户名标志和密码标志为1, 此处没有参考标准协议实现
        String username = msg.payload().userName();
        String password = msg.payload().passwordInBytes() == null ? null : new String(msg.payload().passwordInBytes(), StandardCharsets.UTF_8);
        if (!authService.authByUsernameAndPassword(username, password)) {
            LOGGER.info("DISCONNECT 认证失败 - clientId: {}, cleanSession: {}", msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());

            MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false), null);
            channel.writeAndFlush(connAckMessage);
            channel.close();
            return;
        }


        /**
         * 把之前的给踢下去
         */
        // 如果会话中已存储这个新连接的clientId, 就关闭之前该clientId的连接
        if (iSessionStoreService.containsKey(msg.payload().clientIdentifier())) {
            LOGGER.info("DISCONNECT 踢出之前的连接 - clientId: {}, cleanSession: {}", msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());

            SessionStore sessionStore = iSessionStoreService.get(msg.payload().clientIdentifier());
            Channel previous = sessionStore.getChannel();
            boolean cleanSession = sessionStore.isCleanSession();
            if (cleanSession) {
                iSessionStoreService.remove(msg.payload().clientIdentifier());
                subscribeStoreService.removeForClient(msg.payload().clientIdentifier());
                dupPublishMessageStoreService.removeByClient(msg.payload().clientIdentifier());
                dupPubRelMessageStoreService.removeByClient(msg.payload().clientIdentifier());
            }
            previous.close();
            /**
             * 踢下去以后还要把 上线的缓存给清了
             */

            Channel previousChannel = sessionStore.getChannel();
            if (iChannelStoreService.containsChannelId(previousChannel.id().asLongText())) {
                //System.out.println("设备异常掉线:" + iChannelStoreStoreService.getByChannelId(previousChannel.id().asLongText()));
                //删除Session
                iSessionStoreService.remove(iChannelStoreService.getByChannelId(previousChannel.id().asLongText()).getClientId());
                //删除在线统计
                iChannelStoreService.removeChannelId(previousChannel.id().asLongText());
            }

        }


        /**
         *  处理遗嘱信息
         */
        SessionStore sessionStore = new SessionStore(msg.payload().clientIdentifier(), channel, msg.variableHeader().isCleanSession(), null);
        if (msg.variableHeader().isWillFlag()) {
            MqttPublishMessage willMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(msg.variableHeader().willQos()), msg.variableHeader().isWillRetain(), 0),
                    new MqttPublishVariableHeader(msg.payload().willTopic(), 0), Unpooled.buffer().writeBytes(msg.payload().willMessageInBytes()));
            sessionStore.setWillMessage(willMessage);
        }

        /**
         * 处理连接心跳包
         */
        if (msg.variableHeader().keepAliveTimeSeconds() > 0) {
            if (channel.pipeline().names().contains("idle")) {
                channel.pipeline().remove("idle");
            }
            channel.pipeline().addFirst("idle", new IdleStateHandler(0, 0, Math.round(msg.variableHeader().keepAliveTimeSeconds() * 1.5f)));
        }
        /**
         * 至此存储会话信息及返回接受客户端连接
         */
        iSessionStoreService.put(msg.payload().clientIdentifier(), sessionStore);
        /**
         * 将clientId存储到channel的map中
         */
        channel.attr(AttributeKey.valueOf("clientId")).set(msg.payload().clientIdentifier());
        Boolean sessionPresent = iSessionStoreService.containsKey(msg.payload().clientIdentifier()) && !msg.variableHeader().isCleanSession();
        MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, sessionPresent), null);
        channel.writeAndFlush(okResp);
        LOGGER.info("CONNECT - clientId: {}, cleanSession: {}", msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());
        /**
         * 把channelID保存进去，以便于后面扩展设备上下线问题
         *
         */
        ChannelStore channelStore = new ChannelStore();
        channelStore.setChannelId(channel.id().asLongText());
        channelStore.setClientId(msg.payload().clientIdentifier());
        channelStore.setCleanSession(msg.variableHeader().isCleanSession());
        //channel 转JSON
        JSONObject channelToJson = new JSONObject();
        channelToJson.put("id", channel.id().asLongText());
        channelToJson.put("active", channel.isActive());
        channelToJson.put("address", channel.localAddress());
        //Will 转JSON
        JSONObject willToJson = new JSONObject();
        willToJson.put("willTopic", msg.payload().willTopic());
        willToJson.put("will", JSONObject.parseObject(JSONObject.toJSONString(msg.variableHeader())));
        channelStore.setWillMessageToJson(willToJson);
        channelStore.setChannelToJson(channelToJson);
        iChannelStoreService.putChannelId(channel.id().asLongText(), channelStore);

        /**
         * 如果cleanSession为0, 需要重发同一clientId存储的未完成的QoS1和QoS2的DUP消息
         */
        if (!msg.variableHeader().isCleanSession()) {
            List<DupPublishMessageStore> dupPublishMessageStoreList = dupPublishMessageStoreService.get(msg.payload().clientIdentifier());
            List<DupPubRelMessageStore> dupPubRelMessageStoreList = dupPubRelMessageStoreService.get(msg.payload().clientIdentifier());
            dupPublishMessageStoreList.forEach(dupPublishMessageStore -> {
                MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PUBLISH, true, MqttQoS.valueOf(dupPublishMessageStore.getMqttQoS()), false, 0),
                        new MqttPublishVariableHeader(dupPublishMessageStore.getTopic(), dupPublishMessageStore.getMessageId()), Unpooled.buffer().writeBytes(dupPublishMessageStore.getMessageBytes()));
                channel.writeAndFlush(publishMessage);
            });
            dupPubRelMessageStoreList.forEach(dupPubRelMessageStore -> {
                MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PUBREL, true, MqttQoS.AT_MOST_ONCE, false, 0),
                        MqttMessageIdVariableHeader.from(dupPubRelMessageStore.getMessageId()), null);
                channel.writeAndFlush(pubRelMessage);
            });
        }
    }

    private void closeChannel(Channel channel) {
        MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
        channel.writeAndFlush(connAckMessage);
        channel.close();
    }

}
