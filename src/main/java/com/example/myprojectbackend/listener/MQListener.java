package com.example.myprojectbackend.listener;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MQListener {
    @Resource
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String from;

    @RabbitListener(queues = "simple.queue")
    public void simpleListener(String info){
        Map<String,Object>map= JSONObject.parseObject(info);
        String type=(String) map.get("type");
        String email=(String) map.get("email");
        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(email);
        mailMessage.setSubject(switch (type){
            case "register"->"欢迎注册我们网站";
            case "reset"->"您已重置账户";
            default -> "???";
        });
        mailMessage.setText("您的验证码为："+map.get("code")+"，三分钟内有效，请尽快填写");
        mailSender.send(mailMessage);
    }
}
