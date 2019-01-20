package cn.recallcode.iot.mqtt.server.store.client;

import cn.recallcode.iot.mqtt.server.common.client.IClientService;
import cn.recallcode.iot.mqtt.server.common.session.SessionStore;

import java.util.List;

/**
 * .
 * 统计在线客户端的
 */
public class ClientService implements IClientService {
    @Override
    public void putChannelId(String channelId, SessionStore sessionStore) {

    }

    @Override
    public SessionStore getByChannelId(String channelId) {
        return null;
    }

    @Override
    public boolean containsChannelId(String channelId) {
        return false;
    }

    @Override
    public void removeChannelId(String channelId) {

    }

    @Override
    public int getAllSession() {
        return 0;
    }

    @Override
    public List getAll() {
        return null;
    }
}
