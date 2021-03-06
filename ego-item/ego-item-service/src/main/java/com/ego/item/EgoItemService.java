package com.ego.item;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 *
 **/
@MapperScan("com.ego.item.mapper")
@SpringBootApplication
@EnableDiscoveryClient
public class EgoItemService {
    public static void main(String[] args) {
        SpringApplication.run(EgoItemService.class,args);
    }
}
