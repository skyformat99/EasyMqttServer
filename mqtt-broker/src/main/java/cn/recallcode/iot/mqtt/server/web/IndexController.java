package cn.recallcode.iot.mqtt.server.web;

import cn.recallcode.iot.mqtt.server.store.client.ChannelStoreService;
import cn.recallcode.iot.mqtt.server.store.client.TopicStoreService;
import com.alibaba.fastjson.JSONObject;
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
        return "Run Success!<br> /topics 查看Topic <br> /clients 查看客户端";

    }

    @GetMapping("/topics")
    public Object topics() {
        return JSONObject.toJSONString(topicStoreService.listAll(0, 10));

    }

    @GetMapping("/clients")
    public Object clients() {
        return JSONObject.toJSONString(channelStoreService.listAll(0, 10));

    }


}
