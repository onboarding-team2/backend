package com.team2.onboarding.controller;

import com.team2.onboarding.dto.FindPasswordRequestDto;
import com.team2.onboarding.dto.LoginRequestDto;
import com.team2.onboarding.dto.LoginResponseDto;
import com.team2.onboarding.dto.ResetPasswordRequestDto;
import com.team2.onboarding.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 로그인 (JWT 인가)
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // 비밀번호 찾기
    @PostMapping("/find-password/verify")
    public ResponseEntity<String> verifyCompany(@RequestBody FindPasswordRequestDto request) {
        return ResponseEntity.ok(authService.verifyCompanyForPasswordReset(request));
    }

    // 인증 후 비밀번호 재설정
    @PostMapping("/find-password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDto request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}