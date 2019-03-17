package com.easyiot.iot.mqtt.server.common.client;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import java.util.List;

/**
 * .
 * 统计在线客户端的
 */
@Service
public class ChannelStoreService implements IChannelStoreService {
    @Autowired
    IgniteCache<String, ChannelStore> channelStoreCache;

    @Override
    public void putChannelId(String channelId, ChannelStore channelStore) {
        channelStoreCache.put(channelId, channelStore);

    }

    @Override
    public ChannelStore getByChannelId(String channelId) {
        return channelStoreCache.get(channelId);
    }

    @Override
    public boolean containsChannelId(String channelId) {
        return channelStoreCache.containsKey(channelId);
    }

    @Override
    public void removeChannelId(String channelId) {
        channelStoreCache.remove(channelId);

    }

    @Override
    public int count() {
        return channelStoreCache.size();
    }

    @Override
    public List<Cache.Entry<String, ChannelStore>> listAll(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size < 0) {
            size = 0;
        }
        SqlQuery<String, ChannelStore> query = new SqlQuery<>(ChannelStore.class, String.format("select * from ChannelStore limit %s ,%s", page, size));
        return channelStoreCache.query(query).getAll();
    }



}
