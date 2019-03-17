package com.easyiot.iot.mqtt.server.common.client;

import javax.cache.Cache;
import java.util.List;

/**
 * Topic 缓存
 */
public interface ITopicStoreService {
    /**
     * @param channelId
     * @param topicStore
     */

    void put(String channelId, TopicStore topicStore);

    /**
     * @param channelId
     * @return
     */

    TopicStore get(String channelId);

    /**
     *
     */
    void remove(String channelId);

    /**
     *
     */

    List<Cache.Entry<String, TopicStore>> listAll(int page, int size);

    /**
     *
     */
    int count();

    boolean containsChannelId(String channelId);
}
