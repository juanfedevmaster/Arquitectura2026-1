package com.example.middleware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MiddlewareApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiddlewareApplication.class, args);
    }
}
