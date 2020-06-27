package com.example.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;

/**
 * @author Fatal
 * @date 2020/6/27 0027 11:15
 */
public class RandomRuleConfiguration {

    @Bean
    public IRule rule() {
        return new RandomRule();
    }

}
