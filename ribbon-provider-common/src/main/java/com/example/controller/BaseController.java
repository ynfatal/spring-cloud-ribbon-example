package com.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Fatal
 * @date 2020/6/27 0027 13:56
 */
public class BaseController {

    @Value("${server.port}")
    private Integer port;

    @Value("${spring.application.name}")
    private String name;

    @GetMapping
    public String provider() {
        return String.format("server name : %s, port: %s", name, port);
    }

}
