package com.easyiot.iot.mqtt.server.protocol;

import com.easyiot.iot.mqtt.server.common.auth.IAuthService;
import com.easyiot.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.easyiot.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.easyiot.iot.mqtt.server.common.session.ISessionStoreService;
import com.easyiot.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubAck {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnAck.class);

    private ISessionStoreService sessionStoreService;

    private ISubscribeStoreService subscribeStoreService;

    private IDupPublishMessageStoreService dupPublishMessageStoreService;

    private IDupPubRelMessageStoreService dupPubRelMessageStoreService;

    private IAuthService authService;

    public SubAck(ISessionStoreService sessionStoreService, ISubscribeStoreService subscribeStoreService, IDupPublishMessageStoreService dupPublishMessageStoreService, IDupPubRelMessageStoreService dupPubRelMessageStoreService, IAuthService authService) {

        this.sessionStoreService = sessionStoreService;
        this.subscribeStoreService = subscribeStoreService;
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
        this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
        this.authService = authService;
    }
//    public void processSubAck(Channel channel, MqttConnAckMessage msg) {
//        MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
//                new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
//                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()),
//                new MqttSubAckPayload(mqttQoSList));
//        channel.writeAndFlush(subAckMessage);
//    }


}
