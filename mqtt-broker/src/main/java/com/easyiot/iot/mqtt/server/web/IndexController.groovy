package com.easyiot.iot.mqtt.server.web


import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexController {
    @GetMapping(value = "/", produces = "application/json;charset=UTF-8")
    Object index() {
        return "VUE做的简易后台已经发布了，但是在哪里呢？因为我不会部署前端。。。。。。。。。。。。。。so。。Web控制台请到QQ群下载（滑稽）：475512169"
    }
}
