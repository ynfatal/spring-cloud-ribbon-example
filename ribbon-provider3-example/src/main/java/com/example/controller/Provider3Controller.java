package com.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fatal
 * @date 2020/6/27 0027 9:50
 */
@RestController
public class Provider3Controller {

    @Value("${server.port}")
    private Integer port;

    @Value("${spring.application.name}")
    private String name;

    @GetMapping("/provider3")
    public String provider3() {
        return String.format("server name : %s, port: %s", name, port);
    }

}
