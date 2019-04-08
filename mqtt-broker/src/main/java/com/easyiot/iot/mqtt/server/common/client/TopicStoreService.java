package com.easyiot.iot.mqtt.server.common.client;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import java.util.List;

/**
 * 这个Service的主要功能是把客户端的channelID 和他订阅的Topic 保存下来
 * Key:channelID
 * Value:TopicStore
 */
@Service
public class TopicStoreService implements ITopicStoreService {
    @Autowired
    private IgniteCache<Long, TopicStore> topicStoreCache;

    @Override
    public void save(TopicStore topicStore) {
        topicStoreCache.put(topicStore.getId(), topicStore);
    }

    @Override
    public void remove(long id) {
        topicStoreCache.remove(id);
    }

    @Override
    public void update(TopicStore topicStore) {
        topicStoreCache.put(topicStore.getId(), topicStore);
    }

    @Override
    public List<Cache.Entry<Long, TopicStore>> getByClientId(String clientId) {
        SqlQuery<Long, TopicStore> query = new SqlQuery<Long, TopicStore>(TopicStore.class, "select * from TopicStore where clientId= ? ").setArgs(clientId);

        return topicStoreCache.query(query).getAll();

    }

    @Override
    public TopicStore getById(Long id) {
        SqlQuery<Long, TopicStore> query = new SqlQuery<Long, TopicStore>(TopicStore.class, "select * from TopicStore where id= ? ").setArgs(id);
        query.setPageSize(1);
        if (topicStoreCache.query(query).getAll().size() > 0) {
            return (TopicStore) topicStoreCache.query(query).getAll().get(0);

        } else {
            return null;
        }


    }

    @Override
    public void removeByClientId(String clientId) {
        topicStoreCache.query(new SqlFieldsQuery("DELETE FROM TopicStore where clientId= ?").setArgs(clientId));

    }

    @Override
    public void removeByChannelId(String channelId) {
        topicStoreCache.query(new SqlFieldsQuery("DELETE FROM TopicStore where channelId= ?").setArgs(channelId));

    }


    @Override
    public List<Cache.Entry<Long, TopicStore>> listAll(Pageable pageable) {
        SqlQuery<Long, TopicStore> query = new SqlQuery<Long, TopicStore>(TopicStore.class, "select * from TopicStore limit ? ,?").setArgs(pageable.getPageNumber(), pageable.getPageSize());
        return topicStoreCache.query(query).getAll();

    }

    @Override
    public int count() {
        return topicStoreCache.size();
    }

    @Override
    public boolean containsChannelId(String channelId) {
        SqlQuery<Long, TopicStore> query = new SqlQuery<Long, TopicStore>(TopicStore.class, "select * from TopicStore  where channelId=?").setArgs(channelId);

        return topicStoreCache.query(query).getAll().size() > 0;

    }

}
