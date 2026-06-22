package com.neusoft.nep.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String role;
    private String account;
    private String password;
}
