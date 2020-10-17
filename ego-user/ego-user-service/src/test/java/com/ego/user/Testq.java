package com.ego.user;

import javafx.application.Application;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 *
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class Testq {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @org.junit.Test
    public void test(){
        Object egocode = stringRedisTemplate.opsForHash().get("egocode", "15888903106");
        System.out.println(egocode);

    }
}

