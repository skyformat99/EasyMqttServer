package com.easyiot.iot.mqtt.server.protocol;

import com.easyiot.iot.mqtt.server.common.auth.IAuthService;
import com.easyiot.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.easyiot.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.easyiot.iot.mqtt.server.common.session.ISessionStoreService;
import com.easyiot.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnAck {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnAck.class);

    private ISessionStoreService sessionStoreService;

    private ISubscribeStoreService subscribeStoreService;

    private IDupPublishMessageStoreService dupPublishMessageStoreService;

    private IDupPubRelMessageStoreService dupPubRelMessageStoreService;

    private IAuthService authService;

    public ConnAck(ISessionStoreService sessionStoreService, ISubscribeStoreService subscribeStoreService, IDupPublishMessageStoreService dupPublishMessageStoreService, IDupPubRelMessageStoreService dupPubRelMessageStoreService, IAuthService authService) {

        this.sessionStoreService = sessionStoreService;
        this.subscribeStoreService = subscribeStoreService;
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
        this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
        this.authService = authService;
    }

    public void processConnAck(Channel channel, MqttConnAckMessage msg) {
        MqttMessage pingRespMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false, 0), null, null);
        LOGGER.debug("PINGREQ - clientId: {}", channel.attr(AttributeKey.valueOf("clientId")).get());
        channel.writeAndFlush(pingRespMessage);
    }

}
