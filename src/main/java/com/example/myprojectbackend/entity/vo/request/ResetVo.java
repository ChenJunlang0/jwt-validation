package com.example.myprojectbackend.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetVo {
    @NotEmpty
    @Email
    String email;
    @Size(min = 6,max = 6)
    String code;
    @Size(min = 3,max = 20)
    String password;
}
