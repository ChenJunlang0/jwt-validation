package com.example.myprojectbackend.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    int code;

    T data;

    String message;

    public static <T> Result<T> success(T data){
        return new Result<>(200,data,"sucess");
    }

    public static <T> Result<T> success(T data,String message){return new Result<>(200,data,message);};

    public static <T> Result<T> success(){
        return success(null);
    }

    public static <T> Result<T> failure(int code,String message){
        return new Result<>(code,null,message);
    }

    public static <T> Result<T> unAuthorized(String message){
        return new Result<>(401,null,message);
    }

    public static <T> Result<T>accessDenied(String message){
        return new Result<>(403,null,message);

    }

    public String asJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }


}
