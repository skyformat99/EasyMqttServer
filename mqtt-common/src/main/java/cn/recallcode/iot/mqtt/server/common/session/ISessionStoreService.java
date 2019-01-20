/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.recallcode.iot.mqtt.server.common.session;

import java.util.List;
import java.util.Map;

/**
 * 会话存储服务接口
 */
public interface ISessionStoreService {

    /**
     * 存储会话
     */
    void put(String clientId, SessionStore sessionStore);

    /**
     * 获取会话
     */
    SessionStore get(String clientId);

    /**
     * clientId的会话是否存在
     */
    boolean containsKey(String clientId);

    /**
     * 删除会话
     */
    void remove(String clientId);

    /**
     * 保存channelID
     *
     * @param channelId
     * @param sessionStore
     */
    void putChannelId(String channelId, SessionStore sessionStore);

    /**
     * 根据ChannelId 获取连接进来的客户端
     */

    SessionStore getByChannelId(String channelId);

    /**
     * @param channelId
     * @return
     */
    boolean containsChannelId(String channelId);

    /**
     * 删除ChannelId
     */
    void removeChannelId(String channelId);

    /**
     * 获取所有的数目
     */
    int getChannelCount();

    /**
     * 获取所有
     */
    Map<String, SessionStore> getAll();



}
