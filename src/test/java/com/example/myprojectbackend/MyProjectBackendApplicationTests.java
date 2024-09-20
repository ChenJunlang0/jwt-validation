package com.example.myprojectbackend;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.myprojectbackend.entity.dto.Account;
import com.example.myprojectbackend.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class MyProjectBackendApplicationTests {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    AccountService accountService;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set("man","out",5000, TimeUnit.MICROSECONDS);
    }

    @Test
    void passwordEncode(){
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }

    @Test
    void mpTest(){
        System.out.println(accountService.getById(1));
    }

    @Test
    void sendMail(){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("Test");
        mailMessage.setText("haha");
        mailMessage.setTo("793258183@qq.com");
        mailMessage.setFrom("18206176932@163.com");
        mailSender.send(mailMessage);
    }

    @Test
    void rabbitMQTest(){
        Map<String,Object> map=Map.of("type","register","email","123");
        String jsonString = JSONObject.toJSONString(map);
        Map<String,Object>res=JSONObject.parseObject(jsonString);
        System.out.println(res);
        // rabbitTemplate.convertAndSend("simple.queue2","hey!!!");
    }

    @Test
    void testExist(){
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getEmail,"123");
        System.out.println(accountService.exists(queryWrapper));
    }

    @Test
    void testReset(){
        LambdaUpdateWrapper<Account> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Account::getEmail,"793258183@qq.com").set(Account::getPassword,"12345");
        System.out.println(accountService.update(new LambdaUpdateWrapper<Account>().eq(Account::getEmail,"793258183@qq.com").set(Account::getPassword,"123456")));
    }
}
