package com.neusoft.nep.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phone;
    private String password;
    private String realName;
    private Integer age;
    private String gender;
}
