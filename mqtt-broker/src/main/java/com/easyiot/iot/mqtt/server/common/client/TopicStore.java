package com.easyiot.iot.mqtt.server.common.client;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class TopicStore implements Serializable {
    private static final AtomicLong ID_GEN = new AtomicLong();
    /**
     * Person ID (indexed)
     */
    @QuerySqlField(index = true)
    private long id;

    @QuerySqlField
    private String clientId;
    @QuerySqlField

    private String topicFilter;
    @QuerySqlField
    private String channelId;

    private int mqttQoS;

    public TopicStore(String clientId, String channelId, String topicFilter, int mqttQoS) {
        this.id = ID_GEN.incrementAndGet();
        this.clientId = clientId;
        this.topicFilter = topicFilter;
        this.mqttQoS = mqttQoS;
        this.channelId = channelId;
    }

    public static AtomicLong getIdGen() {
        return ID_GEN;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTopicFilter() {
        return topicFilter;
    }

    public void setTopicFilter(String topicFilter) {
        this.topicFilter = topicFilter;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getMqttQoS() {
        return mqttQoS;
    }

    public void setMqttQoS(int mqttQoS) {
        this.mqttQoS = mqttQoS;
    }
}
