package com.example;

import com.example.config.RandomRuleConfiguration;
import com.example.config.RibbonConfiguration;
import com.example.config.RoundRobinRuleConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Stream;

/**
 * @notes
 *   1. @RibbonClients
 *   public @interface RibbonClients {
 *      // 为服务提供者指定 ribbon client 配置，使用它可以将 ribbon client 配置细化到每个服务
 * 	    RibbonClient[] value() default {};
 *      // 为本服务指定全局的 ribbon client 配置，所有的服务提供者都使用该配置组件
 * 	    Class<?>[] defaultConfiguration() default {};
 *
 *   }
 *   2. @RibbonClient
 *   public @interface RibbonClient {
 *      String value() default "";
 *      // client name
 *      String name() default "";
 *      // 自定义 ribbon client 配置组件，那些组合成 client 的 Bean，可以在这里得到重写（override）。
 *      // 例如：ILoadBalancer、ServerListFilter、IRule。
 *      Class<?>[] configuration() default {};
 *   }
 *
 */
@SpringBootApplication
@EnableEurekaClient
@RibbonClients(value = {
        @RibbonClient(value = "RIBBON-PROVIDER2-EXAMPLE", configuration = RandomRuleConfiguration.class),
        @RibbonClient(value = "RIBBON-PROVIDER3-EXAMPLE", configuration = RoundRobinRuleConfiguration.class)
}, defaultConfiguration = RibbonConfiguration.class)
public class RibbonConsumerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RibbonConsumerExampleApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            System.out.println("看看当前 Spring 容器中的 Bean: ");
            Stream.of(context.getBeanDefinitionNames())
                    .sorted()
                    .forEach(System.out::println);
        };
    }
}
