/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.recallcode.iot.mqtt.server.store.session;

import cn.recallcode.iot.mqtt.server.common.session.ISessionStoreService;
import cn.recallcode.iot.mqtt.server.common.session.SessionStore;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话存储服务
 */
@Service
public class SessionStoreService implements ISessionStoreService {
    @Resource
    private IgniteCache<String, SessionStore> sessionStoreCache;

    //private Map<String, SessionStore> sessionStoreCache = new ConcurrentHashMap<>();

    @Override
    public void put(String clientId, SessionStore sessionStore) {
        sessionStoreCache.put(clientId, sessionStore);
    }

    @Override
    public SessionStore get(String clientId) {
        return sessionStoreCache.get(clientId);
    }

    @Override
    public boolean containsKey(String clientId) {
        return sessionStoreCache.containsKey(clientId);
    }

    @Override
    public void remove(String clientId) {
        sessionStoreCache.remove(clientId);
    }

    /**
     * 保存channelID
     *
     * @param channelId
     * @param sessionStore
     */
    public void putChannelId(String channelId, SessionStore sessionStore) {
        sessionStoreCache.put(channelId, sessionStore);
    }

    @Override
    public SessionStore getByChannelId(String channelId) {
        return sessionStoreCache.get(channelId);
    }

    @Override
    public boolean containsChannelId(String channelId) {
        return sessionStoreCache.containsKey(channelId);
    }

    @Override
    public void removeChannelId(String channelId) {
        sessionStoreCache.remove(channelId);

    }


}
