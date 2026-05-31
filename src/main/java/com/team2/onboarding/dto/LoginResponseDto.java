package com.team2.onboarding.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 로그인 성공시 JWT 토큰 반환용 DTO
@Getter
@NoArgsConstructor
public class LoginResponseDto {
    private String token;
    private String tokenType = "Bearer";

    public LoginResponseDto(String token) {
        this.token = token;
    }
}