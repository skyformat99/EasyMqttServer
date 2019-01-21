package cn.recallcode.iot.mqtt.server.common.client;

import org.apache.ignite.IgniteCache;

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
    void delete(String channelId);

    /**
     *
     */

    IgniteCache<String, TopicStore> all();
}
