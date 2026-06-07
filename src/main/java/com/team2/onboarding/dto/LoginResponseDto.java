package com.team2.onboarding.dto;

import com.team2.onboarding.enums.PlanType;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 로그인 성공시 JWT 토큰 반환용 DTO
@Getter
@NoArgsConstructor
public class LoginResponseDto {
    private String token;
    private String tokenType = "Bearer";
    private PlanType planType;

    public LoginResponseDto(String token, PlanType planType) {
        this.token = token;
        this.planType = planType;
    }
}