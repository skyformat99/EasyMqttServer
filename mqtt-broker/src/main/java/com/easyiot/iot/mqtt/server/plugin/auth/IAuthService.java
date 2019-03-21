/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.easyiot.iot.mqtt.server.plugin.auth;

/**
 * 用户和密码认证服务接口
 */
public interface IAuthService {

    /**
     * 验证用户名和密码是否正确
     */
    boolean authByUsernameAndPassword(String username, String password);

    /**
     * 验证ClientId是否存在
     */
    boolean authByClientId(String clientId);
    /**
     * 根据IP验证
     */
    boolean authByIp(String ipAddress);

}
