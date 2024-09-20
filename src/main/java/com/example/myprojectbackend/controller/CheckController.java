package com.example.myprojectbackend.controller;

import com.example.myprojectbackend.entity.Result;
import com.example.myprojectbackend.utils.JwtUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("check")
public class CheckController {

    @Resource
    JwtUtils jwtUtils;

    @GetMapping("token")
    public Result<Boolean> checkToken(String token){
        boolean isValid = jwtUtils.checkToken(token);
        return Result.success(isValid);
    }
}
