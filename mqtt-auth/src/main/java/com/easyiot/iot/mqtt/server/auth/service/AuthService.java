/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.iot.mqtt.server.auth.service;

import cn.hutool.core.io.IoUtil;
import com.easyiot.iot.mqtt.server.common.auth.IAuthService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.interfaces.RSAPrivateKey;
import java.util.Objects;

/**
 * 用户名和密码认证服务
 */
@Service
public class AuthService implements IAuthService {

    private RSAPrivateKey privateKey;


//    public boolean authRSA(String username, String password) {
//        if (StrUtil.isBlank(username)) return false;
//        if (StrUtil.isBlank(password)) return false;
//        RSA rsa = new RSA(privateKey, null);
//        String value = new String(rsa.encrypt(username, KeyType.PrivateKey));
//        return value.equals(password);
//    }

    @PostConstruct
    public void init() {
        privateKey = IoUtil.readObj(Objects.requireNonNull(AuthService.class.getClassLoader().getResourceAsStream("keystore/auth-private.key")));
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
