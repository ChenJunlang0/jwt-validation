package com.example.myprojectbackend;

import com.example.myprojectbackend.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class MyProjectBackendApplicationTests {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    AccountService accountService;

    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set("man","out",5000, TimeUnit.MICROSECONDS);
    }

    @Test
    void passwordEncode(){
        System.out.println(new BCryptPasswordEncoder().encode("123"));
    }

    @Test
    void mpTest(){
        System.out.println(accountService.getById(1));
    }
}
