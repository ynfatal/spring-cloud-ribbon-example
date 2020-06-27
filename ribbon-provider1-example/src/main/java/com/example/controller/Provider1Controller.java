package com.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fatal
 * @date 2020/6/25 0025 19:34
 */
@RestController
public class Provider1Controller {

    @Value("${server.port}")
    private Integer port;

    @Value("${spring.application.name}")
    private String name;

    @GetMapping("/provider1")
    public String provider1() {
        return String.format("server name : %s, port: %s", name, port);
    }

}
