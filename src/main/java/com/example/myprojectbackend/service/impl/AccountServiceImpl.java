package com.example.myprojectbackend.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myprojectbackend.entity.Result;
import com.example.myprojectbackend.entity.dto.Account;
import com.example.myprojectbackend.entity.vo.request.ResetVo;
import com.example.myprojectbackend.mapper.AccountMapper;
import com.example.myprojectbackend.service.AccountService;
import com.example.myprojectbackend.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    RabbitTemplate rabbitTemplate;

    @Autowired
    StringRedisTemplate redisTemplate;




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = findAccountByNameOrEmail(username);
        if (account==null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    public Account findAccountByNameOrEmail(String text){
        return this.query()
                .eq("username",text).or()
                .eq("email",text)
                .one();
    }

    @Override
    public Result sendCode(String type, String email, String ip) {
        synchronized (ip.intern()){
            if(emailIsInLimit(ip)){
                return Result.failure(408,"请求频繁，请稍后再试");
            }
            Random random=new Random();
            int code=random.nextInt(899999)+100000;
            Map<String,Object>map=Map.of("type",type,"email",email,"code",code);
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.convertAndSend("simple.queue", JSONObject.toJSONString(map));
            redisTemplate.opsForValue().set(Const.CODE_VERIFY_VALID+email,String.valueOf(code),3, TimeUnit.MINUTES);
            return Result.success("验证码发送成功");
        }
    }

    @Override
    public String register(String username, String password, String email, String code) {
        String key=Const.CODE_VERIFY_VALID+email;
        String verifyCode = redisTemplate.opsForValue().get(key);
        if (verifyCode==null)
            return "验证码已过时";
        if (!verifyCode.equals(code))
            return "验证码输入错误";
        if (exists(new LambdaQueryWrapper<Account>().eq(Account::getEmail, email)))
            return "邮箱已注册";
        if (exists(new LambdaQueryWrapper<Account>().eq(Account::getUsername,username)))
            return "用户名已存在";
        Account account = new Account(null, username, new BCryptPasswordEncoder().encode(password), email, "user", new Date());
        if (!this.save(account))
            return "注册失败";
        redisTemplate.delete(key);
        return "注册成功";
    }

    @Override
    public String resetPassword(ResetVo vo) {
        String email = vo.getEmail();
        String code = vo.getCode();
        String verifyCode = redisTemplate.opsForValue().get(Const.CODE_VERIFY_VALID + email);
        if (verifyCode==null)
            return "验证码已过期";
        if (!verifyCode.equals(code))
            return "验证码错误";
        String password = new BCryptPasswordEncoder().encode(vo.getPassword());
        if (!update(new LambdaUpdateWrapper<Account>().eq(Account::getEmail,email).set(Account::getPassword,password))) {
            return "重置密码失败";
        }
        return null;
    }

    private boolean emailIsInLimit(String ip) {
        String res = redisTemplate.opsForValue().get(Const.CODE_VERIFY_TIME_LIMIT + ip);
        if (res==null){
            redisTemplate.opsForValue().set(Const.CODE_VERIFY_TIME_LIMIT+ip,ip,60,TimeUnit.SECONDS);
            return false;
        }
        return true;
    }
}
