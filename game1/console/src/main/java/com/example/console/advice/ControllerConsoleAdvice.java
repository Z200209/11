package com.example.console.advice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@ControllerAdvice(basePackages = "com.example.console.controller")
public class ControllerConsoleAdvice {
    @ExceptionHandler(Exception.class)
    @ResponseBody
        public String handleException(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        log.info(sw.toString());
            return "系统异常";
        }
    }


