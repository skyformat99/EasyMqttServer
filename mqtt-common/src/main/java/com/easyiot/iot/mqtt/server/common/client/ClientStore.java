package com.easyiot.iot.mqtt.server.common.client;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 在线客户端的一个表述
 */

public class ClientStore {
    private static final AtomicLong ID_GEN = new AtomicLong();

    /**
     * Person ID (indexed)
     */
    @QuerySqlField(index = true)
    public long id;
    @QuerySqlField
    private String clientId;
    @QuerySqlField
    private String channelId;
    @QuerySqlField
    private String username;
    @QuerySqlField
    private String password;
    @QuerySqlField
    private String willTopic;
    @QuerySqlField
    private TopicStore topicStore;

    public ClientStore() {
    }

    public ClientStore(String clientId, String channelId, String username, String password, String willTopic, TopicStore topicStore) {
        this.clientId = clientId;
        this.channelId = channelId;
        this.username = username;
        this.password = password;
        this.willTopic = willTopic;
        this.topicStore = topicStore;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWillTopic() {
        return willTopic;
    }

    public void setWillTopic(String willTopic) {
        this.willTopic = willTopic;
    }

    public TopicStore getTopicStore() {
        return topicStore;
    }

    public void setTopicStore(TopicStore topicStore) {
        this.topicStore = topicStore;
    }
}
