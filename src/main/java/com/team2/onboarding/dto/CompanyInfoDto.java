package com.team2.onboarding.dto;

import com.team2.onboarding.enums.PlanType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyInfoDto {

    private String companyName;
    private String businessNumber;
    private PlanType planType;
}
