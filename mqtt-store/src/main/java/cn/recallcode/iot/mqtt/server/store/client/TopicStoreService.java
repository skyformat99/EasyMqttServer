package cn.recallcode.iot.mqtt.server.store.client;

import cn.recallcode.iot.mqtt.server.common.client.ITopicStoreService;
import cn.recallcode.iot.mqtt.server.common.client.TopicStore;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicStoreService implements ITopicStoreService {

    @Autowired
    private IgniteCache<String, TopicStore> topicCache;

    @Override
    public void put(String channelId, TopicStore topicStore) {
        topicCache.put(channelId, topicStore);

    }

    @Override
    public TopicStore get(String channelId) {
        return topicCache.get(channelId);
    }

    @Override
    public void delete(String channelId) {
        topicCache.remove(channelId);
    }

    public IgniteCache<String, TopicStore> all() {
        return topicCache;
    }
}
