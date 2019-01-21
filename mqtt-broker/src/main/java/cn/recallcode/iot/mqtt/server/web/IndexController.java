package cn.recallcode.iot.mqtt.server.web;

import cn.recallcode.iot.mqtt.server.common.client.ITopicStoreService;
import cn.recallcode.iot.mqtt.server.store.client.ChannelStoreService;
import cn.recallcode.iot.mqtt.server.store.client.TopicStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    @Autowired
    ChannelStoreService channelStoreService;
    @Autowired
    TopicStoreService topicStoreService;

    @GetMapping("/")
    public Object index() {

        //channelStoreService.putChannelId("channel" + System.currentTimeMillis(), new ChannelStore());
        //System.out.println(topicStoreService.all());
        return channelStoreService.listAll(0, 10);

    }

}
