package com.example.myprojectbackend.controller;

import com.example.myprojectbackend.entity.Result;
import com.example.myprojectbackend.entity.vo.request.RegisterVo;
import com.example.myprojectbackend.entity.vo.request.ResetVo;
import com.example.myprojectbackend.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@Validated
@RestController
@RequestMapping("api/auth")
public class AuthorizeController {

    @Resource
    AccountService accountService;

    @GetMapping("/ask-code")
    public Result askCode(@RequestParam @Pattern(regexp = "register|reset") String type,
                          @RequestParam @Email @NotEmpty String email,
                          HttpServletRequest request){
        String ip = request.getLocalAddr();

        return  accountService.sendCode(type,email,ip);
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterVo vo){
        String msg=accountService.register(vo.getUsername(),vo.getPassword(),vo.getEmail(),vo.getCode());
        return Result.success(null,msg);
    }

    @PostMapping("/reset")
    public Result<Void> reset(@RequestBody @Valid ResetVo vo){
        String msg=accountService.resetPassword(vo);
        return msg==null?Result.success(null,"重置密码成功"):Result.failure(400,msg);
    }

    @GetMapping("/hello")
    public String hello(){
        System.out.println("man");
        return "hello";
    }
}
