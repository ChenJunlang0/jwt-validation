package com.example.myprojectbackend.exception;

import com.example.myprojectbackend.entity.Result;
import jakarta.validation.ValidationException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
    public Result<Void> validHandler(Exception e){
        return Result.failure(400,"参数错误");
    }
}
