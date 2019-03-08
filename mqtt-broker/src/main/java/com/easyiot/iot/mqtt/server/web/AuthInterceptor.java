package com.easyiot.iot.mqtt.server.web;

import com.alibaba.fastjson.JSONObject;
import com.easyiot.iot.mqtt.server.config.BrokerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 简单安全认证
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    BrokerProperties brokerProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println(request.getRequestURL());

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        String token = request.getHeader("web_console_token");
        if (token == null) {
            JSONObject result = new JSONObject();
            result.put("code", 0);
            result.put("message", "Token required!");
            response.getWriter().write(result.toJSONString());
            return false;
        } else {

            if (token.equals(brokerProperties.getWebConsoleToken())) {
                return true;
            } else {
                JSONObject result = new JSONObject();
                result.put("code", 0);
                result.put("message", "Token Illegal!");
                response.getWriter().write(result.toJSONString());
                return false;
            }


        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {


    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        super.afterConcurrentHandlingStarted(request, response, handler);
    }


}
