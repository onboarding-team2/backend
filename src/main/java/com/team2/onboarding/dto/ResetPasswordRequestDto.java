package com.team2.onboarding.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 새 비밀번호 설정 요청 DTO
@Getter
@NoArgsConstructor
public class ResetPasswordRequestDto {
    private String companyName;
    private String brn;
    private String newPassword;
}