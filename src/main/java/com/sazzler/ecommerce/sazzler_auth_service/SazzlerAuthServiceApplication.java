package com.sazzler.ecommerce.sazzler_auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SazzlerAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SazzlerAuthServiceApplication.class, args);
    }

}
