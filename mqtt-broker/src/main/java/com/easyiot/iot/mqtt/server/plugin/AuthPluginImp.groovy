package com.easyiot.iot.mqtt.server.plugin

import org.springframework.stereotype.Service

@Service
class AuthPluginImp implements AuthPlugin{
    @Override
    boolean authByUsernameAndPassword(String username, String password) {
        return true
    }

    @Override
    boolean authByClientId(String clientId) {
        return true
    }

    @Override
    boolean authByIp(String ipAddress) {
        return true
    }


    @Override
    def version() {
        return "0.0.1"
    }

    @Override
    def name() {
        return "Auth plugin"
    }

}
