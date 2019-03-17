/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.iot.mqtt.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 服务配置
 */
@Component
@ConfigurationProperties(prefix = "spring.mqtt.broker")
public class BrokerProperties {

    /**
     * Broker唯一标识
     */
    private String id;

    /**
     * SSL端口号, 默认8883端口
     */
    private int sslPort = 8885;
    /**
     *
     */
    private int mqttPort = 1883;

    /**
     * WebSocket SSL端口号, 默认9993端口
     */
    private int webSocketSslPort = 9995;

    /**
     * WebSocket Path值, 默认值 /mqtt
     */
    private String webSocketPath = "/web-socket-mqtt";

    /**
     * SSL密钥文件密码
     */
    private String sslPassword;

    /**
     * 心跳时间(秒), 默认60秒, 该值可被客户端连接时相应配置覆盖
     */
    private int keepAlive = 60;

    /**
     * 是否开启Epoll模式, 默认关闭
     */
    private boolean useEpoll = false;

    /**
     * Socket参数, 存放已完成三次握手请求的队列最大长度, 默认511长度
     */
    private int soBacklog = 511;

    /**
     * Socket参数, 是否开启心跳保活机制, 默认开启
     */
    private boolean soKeepAlive = true;

    /**
     * 集群配置, 是否基于组播发现, 默认开启
     */
    private boolean enableMulticastGroup = true;

    /**
     * 集群配置, 基于组播发现
     */
    private String multicastGroup;

    /**
     * 集群配置, 当组播模式禁用时, 使用静态IP开启配置集群
     */
    private String staticIpAddresses;


    /**
     * 认证模式
     * 默认启动的是username-password认证
     * 可以切换成IP认证或者是clientId
     *
     * @return
     */

    private int authType = 1;

    /**
     * UseSSL
     * @return
     */
    private boolean useSSL=false;
    /**
     * WEB 控制台的访问密码
     */
    private String webConsoleToken="qsedftr45g656hbtreQ#Q$RWDQ";

    public String getWebConsoleToken() {
        return webConsoleToken;
    }

    public void setWebConsoleToken(String webConsoleToken) {
        this.webConsoleToken = webConsoleToken;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public String getId() {
        return id;
    }

    public int getMqttPort() {
        return mqttPort;
    }

    public BrokerProperties setMqttPort(int mqttPort) {
        this.mqttPort = mqttPort;
        return this;
    }

    public BrokerProperties setId(String id) {
        this.id = id;
        return this;
    }

    public int getSslPort() {
        return sslPort;
    }

    public BrokerProperties setSslPort(int sslPort) {
        this.sslPort = sslPort;
        return this;
    }

    public int getWebSocketSslPort() {
        return webSocketSslPort;
    }

    public void setWebSocketSslPort(int webSocketSslPort) {
        this.webSocketSslPort = webSocketSslPort;
    }

    public String getWebSocketPath() {
        return webSocketPath;
    }

    public void setWebSocketPath(String webSocketPath) {
        this.webSocketPath = webSocketPath;
    }

    public String getSslPassword() {
        return sslPassword;
    }

    public BrokerProperties setSslPassword(String sslPassword) {
        this.sslPassword = sslPassword;
        return this;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public BrokerProperties setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    public boolean isUseEpoll() {
        return useEpoll;
    }

    public BrokerProperties setUseEpoll(boolean useEpoll) {
        this.useEpoll = useEpoll;
        return this;
    }

    public int getSoBacklog() {
        return soBacklog;
    }

    public BrokerProperties setSoBacklog(int soBacklog) {
        this.soBacklog = soBacklog;
        return this;
    }

    public boolean isSoKeepAlive() {
        return soKeepAlive;
    }

    public BrokerProperties setSoKeepAlive(boolean soKeepAlive) {
        this.soKeepAlive = soKeepAlive;
        return this;
    }

    public boolean isEnableMulticastGroup() {
        return enableMulticastGroup;
    }

    public BrokerProperties setEnableMulticastGroup(boolean enableMulticastGroup) {
        this.enableMulticastGroup = enableMulticastGroup;
        return this;
    }

    public String getMulticastGroup() {
        return multicastGroup;
    }

    public BrokerProperties setMulticastGroup(String multicastGroup) {
        this.multicastGroup = multicastGroup;
        return this;
    }

    public String getStaticIpAddresses() {
        return staticIpAddresses;
    }

    public BrokerProperties setStaticIpAddresses(String staticIpAddresses) {
        this.staticIpAddresses = staticIpAddresses;
        return this;
    }
}
