package com.team2.onboarding.controller;

import com.team2.onboarding.dto.LoginRequestDto;
import com.team2.onboarding.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto request) {
        return authService.login(request);
    }
}