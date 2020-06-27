package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/provider1")
    public String provider1() {
        return restTemplate.getForEntity("http://RIBBON-PROVIDER1-EXAMPLE/provider1", String.class).getBody();
    }

    @GetMapping("/provider2")
    public String provider2() {
        return restTemplate.getForEntity("http://RIBBON-PROVIDER2-EXAMPLE/provider2", String.class).getBody();
    }

    @GetMapping("/provider3")
    public String provider3() {
        return restTemplate.getForEntity("http://RIBBON-PROVIDER3-EXAMPLE/provider3", String.class).getBody();
    }

}
