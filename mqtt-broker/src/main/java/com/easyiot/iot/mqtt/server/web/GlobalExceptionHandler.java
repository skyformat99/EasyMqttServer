package com.easyiot.iot.mqtt.server.web;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseBody
    public Object exceptionHandler(RuntimeException e) {
        if (e instanceof HttpMessageNotReadableException) {
            return ReturnResult.returnTipMessage(0, "Missing parameters!");
        } else {
            return ReturnResult.returnTipMessage(0, e.getMessage());
        }
    }
}