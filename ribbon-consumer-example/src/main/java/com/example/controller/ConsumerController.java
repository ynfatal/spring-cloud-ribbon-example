package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Fatal
 * @date 2020/6/25 0025 21:33
 */
@RestController
public class ConsumerController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public String hello() {
        return restTemplate.getForEntity("http://RIBBON-PROVIDER-EXAMPLE", String.class).getBody();
    }

}