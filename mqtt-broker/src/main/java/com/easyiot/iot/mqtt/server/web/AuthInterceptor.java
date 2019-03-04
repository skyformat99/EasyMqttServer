package com.easyiot.iot.mqtt.server.web;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 简单安全认证
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    private final
    JdbcTemplate jdbcTemplate;

    @Autowired
    public AuthInterceptor(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (token == null) {
            JSONObject result = new JSONObject();
            result.put("code", 0);
            result.put("message", "Token required!");
            response.getWriter().write(result.toJSONString());
            return false;
        } else {
            try {
                jdbcTemplate.queryForMap("SELECT * FROM admin WHERE token=? ", token);
                return true;
            } catch (Exception e) {
                JSONObject result = new JSONObject();
                result.put("code", 0);
                result.put("message", "Admin Not Exist!");
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
