/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.iot.mqtt.server.common.session;

import com.easyiot.iot.mqtt.server.common.session.ISessionStoreService;
import com.easyiot.iot.mqtt.server.common.session.SessionStore;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话存储服务
 */
@Service
public class SessionStoreService implements ISessionStoreService {

    /**
     * 保存会话
     */
    private Map<String, SessionStore> sessionStoreCache = new ConcurrentHashMap<>();

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


}
