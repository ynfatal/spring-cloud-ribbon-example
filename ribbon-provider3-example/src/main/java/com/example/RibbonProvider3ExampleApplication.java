package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class RibbonProvider3ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RibbonProvider3ExampleApplication.class, args);
    }

}
