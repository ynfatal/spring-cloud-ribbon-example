package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Stream;

@SpringBootApplication
@EnableEurekaClient
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
