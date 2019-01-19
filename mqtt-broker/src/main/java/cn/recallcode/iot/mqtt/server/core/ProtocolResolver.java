/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.recallcode.iot.mqtt.server.core;

import cn.recallcode.iot.mqtt.server.internal.InternalCommunication;
import cn.recallcode.iot.mqtt.server.common.auth.IAuthService;
import cn.recallcode.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import cn.recallcode.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import cn.recallcode.iot.mqtt.server.common.message.IMessageIdService;
import cn.recallcode.iot.mqtt.server.common.message.IRetainMessageStoreService;
import cn.recallcode.iot.mqtt.server.common.session.ISessionStoreService;
import cn.recallcode.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import cn.recallcode.iot.mqtt.server.protocol.*;
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
    private IAuthService authService;

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

    public Connect connect() {
        if (connect == null) {
            connect = new Connect(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService, authService);
        }
        return connect;
    }

    public ConnAck connAck() {
        if (connAck == null) {
            connAck = new ConnAck(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService, authService);
        }
        return connAck;

    }

    public Subscribe subscribe() {
        if (subscribe == null) {
            subscribe = new Subscribe(subscribeStoreService, messageIdService, messageStoreService);
        }
        return subscribe;
    }

    public UnSubscribe unSubscribe() {
        if (unSubscribe == null) {
            unSubscribe = new UnSubscribe(subscribeStoreService);
        }
        return unSubscribe;
    }

    public Publish publish() {
        if (publish == null) {
            publish = new Publish(sessionStoreService, subscribeStoreService, messageIdService, messageStoreService, dupPublishMessageStoreService, internalCommunication);
        }
        return publish;
    }

    public DisConnect disConnect() {
        if (disConnect == null) {
            disConnect = new DisConnect(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService);
        }
        return disConnect;
    }

    public PingReq pingReq() {
        if (pingReq == null) {
            pingReq = new PingReq();
        }
        return pingReq;
    }

    public PubRel pubRel() {
        if (pubRel == null) {
            pubRel = new PubRel();
        }
        return pubRel;
    }

    public PubAck pubAck() {
        if (pubAck == null) {
            pubAck = new PubAck(messageIdService, dupPublishMessageStoreService);
        }
        return pubAck;
    }

    public PubRec pubRec() {
        if (pubRec == null) {
            pubRec = new PubRec(dupPublishMessageStoreService, dupPubRelMessageStoreService);
        }
        return pubRec;
    }

    public PubComp pubComp() {
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

    public IAuthService getAuthService() {
        return authService;
    }

    public void setAuthService(IAuthService authService) {
        this.authService = authService;
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
