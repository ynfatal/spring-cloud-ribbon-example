package com.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fatal
 * @date 2020/6/27 0027 6:37
 */
@RestController
public class Provider2Controller {

    @Value("${server.port}")
    private Integer port;

    @Value("${spring.application.name}")
    private String name;

    @GetMapping("/provider2")
    public String provider2() {
        return String.format("server name : %s, port: %s", name, port);
    }

}
