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

    public static <T> Result<T> sucess(T data){
        return new Result<>(200,data,"sucess");
    }

    public static <T> Result<T> sucess(){
        return sucess(null);
    }

    public static <T> Result<T> failure(int code,String message){
        return new Result<>(code,null,message);
    }

    public String asJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }


}
