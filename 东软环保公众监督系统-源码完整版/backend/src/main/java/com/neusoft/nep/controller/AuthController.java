package com.neusoft.nep.controller;
import com.neusoft.nep.dto.LoginRequest;
import com.neusoft.nep.dto.RegisterRequest;
import com.neusoft.nep.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequiredArgsConstructor @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login") public Map<String,Object> login(@RequestBody LoginRequest request) { return authService.login(request); }
    @PostMapping("/register") public Map<String,Object> register(@RequestBody RegisterRequest request) { return authService.register(request); }
}
