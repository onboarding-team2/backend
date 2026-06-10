package com.team2.onboarding.dto;

import com.team2.onboarding.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyInfoDto {
    private String companyName;
    private String businessNumber; // 사업자번호
    private PlanType planType;        // DB형 가입 여부
}