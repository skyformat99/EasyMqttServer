/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.recallcode.iot.mqtt.server.handler;

import cn.recallcode.iot.mqtt.server.common.session.SessionStore;
import cn.recallcode.iot.mqtt.server.core.ProtocolResolver;
import cn.recallcode.iot.mqtt.server.override_netty.MessageReceiveHandler;
import cn.recallcode.iot.mqtt.server.store.session.SessionStoreService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * MQTT消息处理
 */
@Component
public class BrokerHandler extends MessageReceiveHandler<MqttMessage> {

    private ProtocolResolver protocolProcess;
    private SessionStoreService sessionStoreService;

    public BrokerHandler(ProtocolResolver protocolProcess, SessionStoreService sessionStoreService) {
        this.protocolProcess = protocolProcess;
        this.sessionStoreService = sessionStoreService;

    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        //System.out.println("消息：" + msg.fixedHeader());

        switch (msg.fixedHeader().messageType()) {
            /**
             * 链接请求，对应了MQTT 的 CONNECT 报文
             */
            case CONNECT:
                protocolProcess.connect().processConnect(ctx.channel(), (MqttConnectMessage) msg);
                break;
            /**
             * 服务端发送CONNACK报文响应从客户端收到的CONNECT报文。服务端发送给客户端的第一个报文必须是CONNACK [MQTT-3.2.0-1]。
             * 如果客户端在合理的时间内没有收到服务端的CONNACK报文，客户端应该关闭网络连接。合理 的时间取决于应用的类型和通信基础设施。
             *
             */
            case CONNACK:
                protocolProcess.connAck().processConnAck(ctx.channel(), (MqttConnAckMessage) msg);

                break;
            /**
             * 收到客户端的发布消息
             *
             */
            case PUBLISH:
                protocolProcess.publish().processPublish(ctx.channel(), (MqttPublishMessage) msg);
                break;
            /**
             * 发布确认包，也就是：至少保证一次, PUBACK报文是对QoS =1 等级的PUBLISH报文的响应。
             */
            case PUBACK:
                protocolProcess.pubAck().processPubAck(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
                break;
            /**
             * 告诉客户端，你的发布消息我收到了, PUBREC报文是对QoS等级2的PUBLISH报文的响应。它是QoS 2等级协议交换的第二个报文。
             */
            case PUBREC:
                protocolProcess.pubRec().processPubRec(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
                break;
            /**
             * 客户端知道消息收到以后，就释放在内存中的消息， PUBREL报文是对PUBREC报文的响应。它是QoS 2等级协议交换的第三个报文。
             */
            case PUBREL:
                protocolProcess.pubRel().processPubRel(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
                break;
            /**
             * 服务器告诉客户端:你的消息发布完成了
             */
            case PUBCOMP:
                protocolProcess.pubComp().processPubComp(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
                break;
            /**
             * 订阅
             */
            case SUBSCRIBE:
                protocolProcess.subscribe().processSubscribe(ctx.channel(), (MqttSubscribeMessage) msg);
                break;
            /**
             * 订阅回复
             */
            case SUBACK:
                break;
            /**
             * 取消订阅
             */
            case UNSUBSCRIBE:
                protocolProcess.unSubscribe().processUnSubscribe(ctx.channel(), (MqttUnsubscribeMessage) msg);
                break;
            /**
             * 取消订阅回复
             */
            case UNSUBACK:
                break;
            /**
             * 客户端发送PINGREQ报文给服务端的。用于：+
             *
             * 在没有任何其它控制报文从客户端发给服务的时，告知服务端客户端还活着。
             * 请求服务端发送 响应确认它还活着。
             * 使用网络以确认网络连接没有断开。
             * 保持连接（Keep Alive）处理中用到这个报文
             */
            case PINGREQ:
                protocolProcess.pingReq().processPingReq(ctx.channel(), msg);
                break;
            /**
             *  服务端发送PINGRESP报文响应客户端的PINGREQ报文。表示服务端还活着
             */
            case PINGRESP:
                break;
            /**
             * 断开
             */
            case DISCONNECT:
                protocolProcess.disConnect().processDisConnect(ctx.channel(), msg);
                break;


            default:
                break;
        }
    }

    /**
     * 链接过程出现异常
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            // 远程主机强迫关闭了一个现有的连接的异常
            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

    /**
     * 超时的时候响应
     * 1）readerIdleTime：为读超时时间（即测试端一定时间内未接受到被测试端消息）
     * <p>
     * 2）writerIdleTime：为写超时时间（即测试端一定时间内向被测试端发送消息）
     * <p>
     * 3）allIdleTime：所有类型的超时时间
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String channelId = ctx.channel().id().asLongText();

        if (sessionStoreService.containsChannelId(channelId)) {
            System.out.println(sessionStoreService.getByChannelId(channelId));
        }

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                Channel channel = ctx.channel();
                String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
                // 发送遗嘱消息
                if (this.protocolProcess.getSessionStoreService().containsKey(clientId)) {
                    SessionStore sessionStore = this.protocolProcess.getSessionStoreService().get(clientId);
                    if (sessionStore.getWillMessage() != null) {
                        this.protocolProcess.publish().processPublish(ctx.channel(), sessionStore.getWillMessage());
                    }
                }
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


}
