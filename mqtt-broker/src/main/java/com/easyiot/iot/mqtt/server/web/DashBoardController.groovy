package com.easyiot.iot.mqtt.server.web

import com.alibaba.fastjson.JSONObject
import com.easyiot.iot.mqtt.server.common.client.ChannelStore
import com.easyiot.iot.mqtt.server.common.client.ChannelStoreService
import com.easyiot.iot.mqtt.server.common.client.TopicStoreService
import com.easyiot.iot.mqtt.server.config.BrokerProperties
import com.easyiot.iot.mqtt.server.plugin.PluginLoader
import org.apache.ignite.Ignite
import org.apache.ignite.cluster.ClusterNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping(value = "/dashboard")
class DashBoardController {
    @Autowired
    Ignite ignite
    @Autowired
    ChannelStoreService channelStoreService
    @Autowired
    TopicStoreService topicStoreService
    @Autowired
    BrokerProperties brokerProperties

    @PostMapping("/login")
    Object login(@RequestBody JSONObject body) {
        if (body.getString("web_console_token").length() > 0 &&
                body.getString("web_console_token").equals(brokerProperties.getWebConsoleToken())) {

            return [state: 1, "message": "Login success!", token: brokerProperties.getWebConsoleToken()]
        } else {

            return [state: 0, "message": "Login failure!"]


        }

    }

    @GetMapping("/topics")
    Object topics(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return [state: 1, "message": "Success!", total: topicStoreService.count(), data: topicStoreService.listAll(page, size)]

    }

    @GetMapping("/searchTopicByChannelId")
    Object searchTopicByChannelId(@RequestParam String channelId) {
        return [state: 1, "message": "Success!", total: topicStoreService.count(), data: topicStoreService.get(channelId)]
    }


    @GetMapping("/channels")

    Object channels(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

        return [state: 1, "message": "Success!", total: channelStoreService.count(), data: channelStoreService.listAll(page, size)]

    }


    /**
     * 本地节点信息
     *
     * @return
     */
    @GetMapping("/localNodeInfo")
    Object localNodeInfo() {
        try {
            ClusterNode localNode = ignite.cluster().localNode()
            ClusterNodeInfo clusterNodeInfo = new ClusterNodeInfo(localNode)
            return ReturnResult.returnDataMessage(1, "Success", JSONObject.toJSON(clusterNodeInfo))
        } catch (Exception e) {
            return [state: 0, "message": "Info access failure,Because of internal error!"]

        }


    }
    /**
     * 获取版本号
     * @return
     */

    @GetMapping(value = "/version")
    def version() {
        return [state: 1, data: [version: "0.0.1", runState: "running"], "message": "Success"]
    }
    /**
     * 查询
     * @param body
     * @return
     */
    @PostMapping(value = "/searchChannel")
    def searchChannel(@RequestBody JSONObject body) {
        def channelId = body.getString("channelId")
        if (!channelId) {
            return [state: 0, "message": "Missing parameter: channelId !"]
        } else {
            ChannelStore channel = channelStoreService.getByChannelId(channelId)
            if (channel) {
                return [state: 1, "message": "Success!", data: channel]
            } else {
                return [state: 0, "message": "Channel not exists!"]
            }
        }

    }

    @Autowired
    PluginLoader pluginLoader

    @GetMapping(value = "/plugins")
    def plugins() {
        return [state: 1, "message": "Success", data: pluginLoader.pluginList()]
    }
}
