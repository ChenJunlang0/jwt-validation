package com.example.myprojectbackend.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterVo {
    @Size(min = 3,max = 14)
    String username;
    @Size(min = 3,max = 20)
    String password;
    @Email
    String email;
    @Size(min = 6,max = 6)
    String code;
}
