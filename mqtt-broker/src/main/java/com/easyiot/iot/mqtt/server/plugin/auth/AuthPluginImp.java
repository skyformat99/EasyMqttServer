package com.easyiot.iot.mqtt.server.plugin.auth;

import org.springframework.stereotype.Service;

@Service
public class AuthPluginImp implements AuthPlugin {
    @Override
    public String version() {
        return "0.0.1";
    }

    @Override
    public String name() {
        return "Auth plugin";
    }

    @Override
    public boolean authByUsernameAndPassword(String username, String password) {
        return true;
    }

    @Override
    public boolean authByClientId(String clientId) {
        return true;
    }

    @Override
    public boolean authByIp(String ipAddress) {
        return true;
    }

}
