package com.easyiot.iot.mqtt.server.common.client;

import org.springframework.data.domain.Pageable;

import javax.cache.Cache;
import java.util.List;

/**
 * Topic 缓存
 */
public interface ITopicStoreService {

    void save(TopicStore topicStore);

    void remove(long id);

    void update(TopicStore topicStore);


    List<List<?>> getByClientId(String clientId);

    TopicStore getById(Long id);


    void removeByClientId(String clientId);

    void removeByChannelId(String channelId);

    List<Cache.Entry<Long, TopicStore>> listAll(Pageable pageable);

    int count();


    boolean containsChannelId(String channelId);
}
