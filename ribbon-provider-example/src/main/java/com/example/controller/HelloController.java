package com.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fatal
 * @date 2020/6/25 0025 19:34
 */
@RestController
public class HelloController {

    @Value("${server.port}")
    private Integer port;

    @GetMapping
    public String hello() {
        return "hello, have a good time! port: " + port;
    }

}
