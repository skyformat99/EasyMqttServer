/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.iot.mqtt.server.core;

import com.easyiot.iot.mqtt.server.common.client.IChannelStoreService;
import com.easyiot.iot.mqtt.server.common.client.ITopicStoreService;
import com.easyiot.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.easyiot.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.easyiot.iot.mqtt.server.common.message.IMessageIdService;
import com.easyiot.iot.mqtt.server.common.message.IRetainMessageStoreService;
import com.easyiot.iot.mqtt.server.common.session.ISessionStoreService;
import com.easyiot.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import com.easyiot.iot.mqtt.server.internal.InternalCommunication;
import com.easyiot.iot.mqtt.server.plugin.auth.AuthPlugin;
import com.easyiot.iot.mqtt.server.plugin.auth.MessagePersistencePlugin;
import com.easyiot.iot.mqtt.server.protocol.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 协议处理
 */
@Component
public class ProtocolResolver {

    @Autowired
    private ISessionStoreService sessionStoreService;

    @Autowired
    private ISubscribeStoreService subscribeStoreService;

    @Autowired
    private IMessageIdService messageIdService;

    @Autowired
    private IRetainMessageStoreService messageStoreService;

    @Autowired
    private IDupPublishMessageStoreService dupPublishMessageStoreService;

    @Autowired
    private IDupPubRelMessageStoreService dupPubRelMessageStoreService;

    @Autowired
    private InternalCommunication internalCommunication;
    @Autowired
    IChannelStoreService iChannelStoreService;

    @Autowired
    AuthPlugin authPlugin;

    @Autowired
    private ITopicStoreService topicStoreService;

    @Autowired
    private MessagePersistencePlugin messagePersistencePlugin;
    private Connect connect;

    private ConnAck connAck;

    private Subscribe subscribe;

    private UnSubscribe unSubscribe;

    private Publish publish;

    private DisConnect disConnect;

    private PingReq pingReq;

    private PubRel pubRel;

    private PubAck pubAck;

    private PubRec pubRec;

    private PubComp pubComp;

    Connect connect() {
        if (connect == null) {
            connect = new Connect(iChannelStoreService, sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService, authPlugin);
        }
        return connect;
    }

    ConnAck connAck() {
        if (connAck == null) {
            connAck = new ConnAck(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService, authPlugin);
        }
        return connAck;

    }

    public Subscribe subscribe() {
        if (subscribe == null) {
            subscribe = new Subscribe(subscribeStoreService, messageIdService, messageStoreService, topicStoreService);
        }
        return subscribe;
    }

    UnSubscribe unSubscribe() {
        if (unSubscribe == null) {
            unSubscribe = new UnSubscribe(subscribeStoreService, topicStoreService);
        }
        return unSubscribe;
    }

    Publish publish() {
        if (publish == null) {
            publish = new Publish(sessionStoreService, subscribeStoreService, messageIdService, messageStoreService, dupPublishMessageStoreService, internalCommunication, messagePersistencePlugin);
        }
        return publish;
    }

    DisConnect disConnect() {
        if (disConnect == null) {
            disConnect = new DisConnect(sessionStoreService,
                    subscribeStoreService,
                    dupPublishMessageStoreService,
                    dupPubRelMessageStoreService,
                    iChannelStoreService,
                    sessionStoreService);
        }
        return disConnect;
    }

    PingReq pingReq() {
        if (pingReq == null) {
            pingReq = new PingReq();
        }
        return pingReq;
    }

    PubRel pubRel() {
        if (pubRel == null) {
            pubRel = new PubRel();
        }
        return pubRel;
    }

    PubAck pubAck() {
        if (pubAck == null) {
            pubAck = new PubAck(messageIdService, dupPublishMessageStoreService);
        }
        return pubAck;
    }

    PubRec pubRec() {
        if (pubRec == null) {
            pubRec = new PubRec(dupPublishMessageStoreService, dupPubRelMessageStoreService);
        }
        return pubRec;
    }

    PubComp pubComp() {
        if (pubComp == null) {
            pubComp = new PubComp(messageIdService, dupPubRelMessageStoreService);
        }
        return pubComp;
    }

    public ISessionStoreService getSessionStoreService() {
        return sessionStoreService;
    }

    public void setSessionStoreService(ISessionStoreService sessionStoreService) {
        this.sessionStoreService = sessionStoreService;
    }

    public ISubscribeStoreService getSubscribeStoreService() {
        return subscribeStoreService;
    }

    public void setSubscribeStoreService(ISubscribeStoreService subscribeStoreService) {
        this.subscribeStoreService = subscribeStoreService;
    }

    public IMessageIdService getMessageIdService() {
        return messageIdService;
    }

    public void setMessageIdService(IMessageIdService messageIdService) {
        this.messageIdService = messageIdService;
    }

    public IRetainMessageStoreService getMessageStoreService() {
        return messageStoreService;
    }

    public void setMessageStoreService(IRetainMessageStoreService messageStoreService) {
        this.messageStoreService = messageStoreService;
    }

    public IDupPublishMessageStoreService getDupPublishMessageStoreService() {
        return dupPublishMessageStoreService;
    }

    public void setDupPublishMessageStoreService(IDupPublishMessageStoreService dupPublishMessageStoreService) {
        this.dupPublishMessageStoreService = dupPublishMessageStoreService;
    }

    public IDupPubRelMessageStoreService getDupPubRelMessageStoreService() {
        return dupPubRelMessageStoreService;
    }

    public void setDupPubRelMessageStoreService(IDupPubRelMessageStoreService dupPubRelMessageStoreService) {
        this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
    }

    public InternalCommunication getInternalCommunication() {
        return internalCommunication;
    }

    public void setInternalCommunication(InternalCommunication internalCommunication) {
        this.internalCommunication = internalCommunication;
    }

    public IChannelStoreService getiChannelStoreService() {
        return iChannelStoreService;
    }

    public void setiChannelStoreService(IChannelStoreService iChannelStoreService) {
        this.iChannelStoreService = iChannelStoreService;
    }

    public AuthPlugin getAuthPlugin() {
        return authPlugin;
    }

    public void setAuthPlugin(AuthPlugin authPlugin) {
        this.authPlugin = authPlugin;
    }

    public ITopicStoreService getTopicStoreService() {
        return topicStoreService;
    }

    public void setTopicStoreService(ITopicStoreService topicStoreService) {
        this.topicStoreService = topicStoreService;
    }

    public MessagePersistencePlugin getMessagePersistencePlugin() {
        return messagePersistencePlugin;
    }

    public void setMessagePersistencePlugin(MessagePersistencePlugin messagePersistencePlugin) {
        this.messagePersistencePlugin = messagePersistencePlugin;
    }

    public Connect getConnect() {
        return connect;
    }

    public void setConnect(Connect connect) {
        this.connect = connect;
    }

    public ConnAck getConnAck() {
        return connAck;
    }

    public void setConnAck(ConnAck connAck) {
        this.connAck = connAck;
    }

    public Subscribe getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Subscribe subscribe) {
        this.subscribe = subscribe;
    }

    public UnSubscribe getUnSubscribe() {
        return unSubscribe;
    }

    public void setUnSubscribe(UnSubscribe unSubscribe) {
        this.unSubscribe = unSubscribe;
    }

    public Publish getPublish() {
        return publish;
    }

    public void setPublish(Publish publish) {
        this.publish = publish;
    }

    public DisConnect getDisConnect() {
        return disConnect;
    }

    public void setDisConnect(DisConnect disConnect) {
        this.disConnect = disConnect;
    }

    public PingReq getPingReq() {
        return pingReq;
    }

    public void setPingReq(PingReq pingReq) {
        this.pingReq = pingReq;
    }

    public PubRel getPubRel() {
        return pubRel;
    }

    public void setPubRel(PubRel pubRel) {
        this.pubRel = pubRel;
    }

    public PubAck getPubAck() {
        return pubAck;
    }

    public void setPubAck(PubAck pubAck) {
        this.pubAck = pubAck;
    }

    public PubRec getPubRec() {
        return pubRec;
    }

    public void setPubRec(PubRec pubRec) {
        this.pubRec = pubRec;
    }

    public PubComp getPubComp() {
        return pubComp;
    }

    public void setPubComp(PubComp pubComp) {
        this.pubComp = pubComp;
    }
}
