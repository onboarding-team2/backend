package com.team2.onboarding.dto;

import com.team2.onboarding.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDetailDto {

    // 회사 기본 정보
    private String companyName;
    private String businessNumber;
    private String representativeName;
    private PlanType planType;

    // 퇴직연금 정보 (공통)
    private String companyAccount;
    private LocalDate contractDate;
    private Long employeeCount; // 가입자 수
    private Long totalReserve; // 총 적립금

    // DC 전용
    private String paymentCycle; // 납입주기 (연납/분기납/월납)

    // DB 전용
    private Integer fiscalMonth; // 결산월
    private BigDecimal targetReturnRate; // 목표수익률
}
