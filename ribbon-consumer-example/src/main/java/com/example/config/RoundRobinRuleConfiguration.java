package com.example.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.context.annotation.Bean;

/**
 * 局部负载均衡规则配置类（在被 @RibbonClient 指定之后变成配置组件）
 * @implNote 对服务提供者的 ribbon client 配置可以细化到具体某个服务，只需使用 @RibbonClient 指定即可。
 * 这种情况下的配置类不需要标注为配置组件，也就是不需要加 @Configuration，因为 @RibbonClient 会帮我们
 * 实例化（只实例化一次）这个配置组件，这种方式实例化的配置组件包括其下的 Bean 都没有托管 Spring 容器。
 * （已测。ApplicationContext 拿不到这个 Bean 的，因为它不在 @ComponentScan 的扫描范围内，等下测试 @Bean 去掉有没有影响）
 * @author Fatal
 * @date 2020/6/27 0027 8:10
 */
public class RoundRobinRuleConfiguration {

    @Bean
    public IRule rule() {
        return new RoundRobinRule();
    }

}
