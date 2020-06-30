package com.example.controller;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Fatal
 * @date 2020/6/25 0025 21:33
 */
@Slf4j
@RestController
@AllArgsConstructor
public class ConsumerController {

    private RestTemplate restTemplate;
    private SpringClientFactory springClientFactory;

    @GetMapping("/ribbon_context")
    private SpringClientFactory ribbonContext() {
        log.info("contextNames = [{}]", springClientFactory.getContextNames());
        log.info("RIBBON-PROVIDER1-EXAMPLE's rule is [{}]", springClientFactory.getInstance("RIBBON-PROVIDER1-EXAMPLE", IRule.class));
        log.info("RIBBON-PROVIDER1-EXAMPLE's ILoadBalancer is [{}]", springClientFactory.getInstance("RIBBON-PROVIDER1-EXAMPLE", ILoadBalancer.class));
        log.info("RIBBON-PROVIDER2-EXAMPLE's rule is [{}]", springClientFactory.getInstance("RIBBON-PROVIDER2-EXAMPLE", IRule.class));
        log.info("RIBBON-PROVIDER2-EXAMPLE's ILoadBalancer is [{}]", springClientFactory.getInstance("RIBBON-PROVIDER2-EXAMPLE", ILoadBalancer.class));
        log.info("RIBBON-PROVIDER3-EXAMPLE's rule is [{}]", springClientFactory.getInstance("RIBBON-PROVIDER3-EXAMPLE", IRule.class));
        log.info("RIBBON-PROVIDER3-EXAMPLE's ILoadBalancer is [{}]", springClientFactory.getInstance("RIBBON-PROVIDER3-EXAMPLE", ILoadBalancer.class));
        return springClientFactory;
    }

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
