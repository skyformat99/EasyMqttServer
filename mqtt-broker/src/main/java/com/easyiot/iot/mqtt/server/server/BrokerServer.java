/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.iot.mqtt.server.server;

import com.easyiot.iot.mqtt.server.codec.MqttWebSocketCodec;
import com.easyiot.iot.mqtt.server.common.client.IChannelStoreStoreService;
import com.easyiot.iot.mqtt.server.common.client.ITopicStoreService;
import com.easyiot.iot.mqtt.server.common.session.ISessionStoreService;
import com.easyiot.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import com.easyiot.iot.mqtt.server.config.BrokerProperties;
import com.easyiot.iot.mqtt.server.core.ProtocolResolver;
import com.easyiot.iot.mqtt.server.handler.BrokerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * Netty启动Broker
 */
@Component
public class BrokerServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServer.class);

    @Autowired
    private BrokerProperties brokerProperties;

    @Autowired
    private ProtocolResolver protocolProcess;

    @Autowired
    private IChannelStoreStoreService iChannelStoreStoreService;

    @Autowired
    private ISessionStoreService iSessionStoreService;

    @Autowired
    ISubscribeStoreService iSubscribeStoreService;

    @Autowired
    ITopicStoreService iTopicStoreService;


    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private SslContext sslContext;

    private Channel channel;

    private Channel websocketChannel;

    @PostConstruct
    public void start() throws Exception {
        LOGGER.info("Initializing {} MQTT Broker ...", "[" + brokerProperties.getId() + "]");
        bossGroup = brokerProperties.isUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        workerGroup = brokerProperties.isUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("keystore/mqtt-broker.pfx");
        keyStore.load(inputStream, brokerProperties.getSslPassword().toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keyStore, brokerProperties.getSslPassword().toCharArray());
        sslContext = SslContextBuilder.forServer(kmf).build();
        mqttServer();
        webSocketServer();
        LOGGER.info("MQTT Broker {} is up and running. Open SSLPort: {} WebSocketSSLPort: {}", "[" + brokerProperties.getId() + "]", brokerProperties.getSslPort(), brokerProperties.getWebsocketSslPort());
    }

    @PreDestroy
    public void stop() {
        LOGGER.info("Shutdown {} MQTT Broker ...", "[" + brokerProperties.getId() + "]");
        bossGroup.shutdownGracefully();
        bossGroup = null;
        workerGroup.shutdownGracefully();
        workerGroup = null;
        channel.closeFuture().syncUninterruptibly();
        channel = null;
        websocketChannel.closeFuture().syncUninterruptibly();
        websocketChannel = null;
        LOGGER.info("MQTT Broker {} shutdown finish.", "[" + brokerProperties.getId() + "]");
    }

    /**
     * 加载MQTT服务器
     * @throws Exception
     */
    private void mqttServer() throws Exception {
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(bossGroup, workerGroup)
                .channel(brokerProperties.isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                // handler在初始化时就会执行
                .handler(new LoggingHandler(LogLevel.INFO))
                // childHandler会在客户端成功connect后才执行
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        // Netty提供的心跳检测
                        /**
                         * 我们在channel链中加入了IdleSateHandler，第一个参数是5，单位是秒，那么这样做的意思就是：
                         * 在服务器端会每隔5秒来检查一下channelRead方法被调用的情况，如果在5秒内该链上的channelRead方法都没有被触发，
                         * 就会调用userEventTriggered方法
                         */
                        channelPipeline.addFirst("idle", new IdleStateHandler(0, 0, brokerProperties.getKeepAlive()));
                        // Netty提供的SSL处理
                        /**
                         * SSL开关
                         */
                        loadSSL(channelPipeline, socketChannel);
                        channelPipeline.addLast("decoder", new MqttDecoder());
                        channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
                        channelPipeline.addLast("broker", new BrokerHandler(protocolProcess, iChannelStoreStoreService, iSessionStoreService, iSubscribeStoreService, iTopicStoreService));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, brokerProperties.getSoBacklog())
                .childOption(ChannelOption.SO_KEEPALIVE, brokerProperties.isSoKeepAlive());
        channel = sb.bind(brokerProperties.getSslPort()).sync().channel();
    }

    /**
     * 加载Web Socket服务器
     * @throws Exception
     */
    private void webSocketServer() throws Exception {
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(bossGroup, workerGroup)
                .channel(brokerProperties.isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                // handler在初始化时就会执行
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        // Netty提供的心跳检测
                        channelPipeline.addFirst("idle", new IdleStateHandler(0, 0, brokerProperties.getKeepAlive()));

                        loadSSL(channelPipeline, socketChannel);

                        // 将请求和应答消息编码或解码为HTTP消息
                        channelPipeline.addLast("http-codec", new HttpServerCodec());
                        // 将HTTP消息的多个部分合成一条完整的HTTP消息
                        channelPipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                        // 将HTTP消息进行压缩编码
                        channelPipeline.addLast("compressor ", new HttpContentCompressor());
                        channelPipeline.addLast("protocol", new WebSocketServerProtocolHandler(brokerProperties.getWebsocketPath(), "mqtt,mqttv3.1,mqttv3.1.1", true, 65536));
                        channelPipeline.addLast("mqttWebSocket", new MqttWebSocketCodec());
                        channelPipeline.addLast("decoder", new MqttDecoder());
                        channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
                        channelPipeline.addLast("broker", new BrokerHandler(protocolProcess, iChannelStoreStoreService, iSessionStoreService, iSubscribeStoreService, iTopicStoreService));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, brokerProperties.getSoBacklog())
                .childOption(ChannelOption.SO_KEEPALIVE, brokerProperties.isSoKeepAlive());
        websocketChannel = sb.bind(brokerProperties.getWebsocketSslPort()).sync().channel();
    }

    private void loadSSL(ChannelPipeline channelPipeline, SocketChannel socketChannel) {
        if (brokerProperties.isUseSSL()) {
            SSLEngine sslEngine = sslContext.newEngine(socketChannel.alloc());
            sslEngine.setUseClientMode(false);        // 服务端模式
            sslEngine.setNeedClientAuth(false);        // 不需要验证客户端
            channelPipeline.addLast("ssl", new SslHandler(sslEngine));

        }

    }
}
