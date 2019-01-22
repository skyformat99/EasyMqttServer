package cn.recallcode.iot.mqtt.server.common.client;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;

public class TopicStore implements Serializable {

    @QuerySqlField
    private String clientId;
    @QuerySqlField(index = true)

    private String topicFilter;

    private int mqttQoS;

    public TopicStore() {
    }

    public TopicStore(String clientId, String topicFilter, int mqttQoS) {
        this.clientId = clientId;
        this.topicFilter = topicFilter;
        this.mqttQoS = mqttQoS;
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

    public int getMqttQoS() {
        return mqttQoS;
    }

    public void setMqttQoS(int mqttQoS) {
        this.mqttQoS = mqttQoS;
    }
}
