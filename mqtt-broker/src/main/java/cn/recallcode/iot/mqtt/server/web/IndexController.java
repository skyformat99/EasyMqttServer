package cn.recallcode.iot.mqtt.server.web;

import cn.recallcode.iot.mqtt.server.store.session.SessionStoreService;
import cn.recallcode.iot.mqtt.server.store.subscribe.SubscribeStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexController {
    @Autowired
    SessionStoreService sessionStoreService;
    @Autowired
    SubscribeStoreService subscribeStoreService;

    @GetMapping("/")
    public Object index() {

        Map<String, Object> map = new HashMap<>();
        map.put("online", sessionStoreService.getChannelCount());
        map.put("qos1", subscribeStoreService.getNotWildcardCacheTopicCount());
        map.put("qos2", subscribeStoreService.getWildcardCacheTopicCount());
        return map;
    }

}
