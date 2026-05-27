package com.team2.onboarding.controller;

import com.team2.onboarding.dto.FindPasswordRequestDto;
import com.team2.onboarding.dto.LoginRequestDto;
import com.team2.onboarding.dto.LoginResponseDto;
import com.team2.onboarding.dto.ResetPasswordRequestDto;
import com.team2.onboarding.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // [Authorization 추가 버전] 로그인 API (성공 시 토큰 발급)
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // 1. 비밀번호 찾기 - 기업 본인인증 단계 API
    @PostMapping("/find-password/verify")
    public ResponseEntity<String> verifyCompany(@RequestBody FindPasswordRequestDto request) {
        return ResponseEntity.ok(authService.verifyCompanyForPasswordReset(request));
    }

    // 1-2. 비밀번호 찾기 - 인증 후 새 비밀번호 저장 API
    @PostMapping("/find-password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDto request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}