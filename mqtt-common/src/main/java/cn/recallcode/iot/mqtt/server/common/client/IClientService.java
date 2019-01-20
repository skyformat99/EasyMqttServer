package cn.recallcode.iot.mqtt.server.common.client;

import cn.recallcode.iot.mqtt.server.common.session.SessionStore;

import java.util.List;

public interface IClientService {

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
    int getAllSession();

    /**
     * 获取所有
     */
    List getAll();

}
