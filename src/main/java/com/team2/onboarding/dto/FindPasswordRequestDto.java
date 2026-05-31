package com.team2.onboarding.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 비밀번호 찾기(본인 확인) 요청 DTO
@Getter
@NoArgsConstructor
public class FindPasswordRequestDto {
    private String companyName;
    private String brn;
}
