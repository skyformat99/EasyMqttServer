package com.easyiot.iot.mqtt.server.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class IndexController {
    @GetMapping("/")
    Object index() {
        return "index"
    }
}
