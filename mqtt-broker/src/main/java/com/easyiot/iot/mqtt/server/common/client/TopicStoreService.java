package com.easyiot.iot.mqtt.server.common.client;

import com.easyiot.iot.mqtt.server.common.client.ITopicStoreService;
import com.easyiot.iot.mqtt.server.common.client.TopicStore;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import java.util.List;

@Service
public class TopicStoreService implements ITopicStoreService {

    @Autowired
    private IgniteCache<String, TopicStore> topicStoreCache;

    @Override
    public void put(String channelId, TopicStore topicStore) {
        topicStoreCache.put(channelId, topicStore);

    }

    @Override
    public TopicStore get(String channelId) {
        return topicStoreCache.get(channelId);
    }

    @Override
    public void remove(String channelId) {
        topicStoreCache.remove(channelId);
    }


    @Override
    public List<Cache.Entry<String, TopicStore>> listAll(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size < 0) {
            size = 0;
        }
        SqlQuery<String, TopicStore> query = new SqlQuery<>(TopicStore.class, String.format("select * from TopicStore limit %s ,%s", page, size));


        return topicStoreCache.query(query).getAll();
    }

    @Override
    public int count() {
        return topicStoreCache.size();
    }
}
