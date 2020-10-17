package com.ego.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
/**
 *
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class EgoSmsService {
    public static void main(String[] args) {
        SpringApplication.run(EgoSmsService.class, args);
    }
}
