package cn.recallcode.iot.mqtt.server.common.client;

import com.alibaba.fastjson.JSONObject;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;

/**
 * 在线设备
 */
public class ChannelStore implements Serializable {
    @QuerySqlField
    private String clientId;
    @QuerySqlField(index = true)
    private String channelId;

    private JSONObject channelToJson;

    private boolean cleanSession;

    private JSONObject willMessageToJson;

    public ChannelStore() {

    }

    public ChannelStore(String clientId, String channelId, JSONObject channelToJson, boolean cleanSession, JSONObject willMessageToJson) {
        this.clientId = clientId;
        this.channelId = channelId;
        this.channelToJson = channelToJson;
        this.cleanSession = cleanSession;
        this.willMessageToJson = willMessageToJson;
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

    public JSONObject getChannelToJson() {
        return channelToJson;
    }

    public void setChannelToJson(JSONObject channelToJson) {
        this.channelToJson = channelToJson;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public JSONObject getWillMessageToJson() {
        return willMessageToJson;
    }

    public void setWillMessageToJson(JSONObject willMessageToJson) {
        this.willMessageToJson = willMessageToJson;
    }

    @Override
    public String toString() {
        return "ChannelStore{" +
                "clientId='" + clientId + '\'' +
                ", channelId='" + channelId + '\'' +
                ", channelToJson=" + channelToJson +
                ", cleanSession=" + cleanSession +
                ", willMessage=" + willMessageToJson +
                '}';
    }
}
