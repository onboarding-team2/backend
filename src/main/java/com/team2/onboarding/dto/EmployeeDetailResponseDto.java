package com.team2.onboarding.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/** 가입자 상세 조회 응답. */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeDetailResponseDto {

    private Long id;
    private String memberId;
    private String name;
    private String rrnMasked;           // 주민번호는 마스킹해서 내려준다
    private CompanyInfo company;
    private RetirementInfo retirement;
    private List<AnnualSalaryDto> annualSalaries;

    @Builder
    private EmployeeDetailResponseDto(
            Long id,
            String memberId,
            String name,
            String rrnMasked,
            CompanyInfo company,
            RetirementInfo retirement,
            List<AnnualSalaryDto> annualSalaries
    ) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.rrnMasked = rrnMasked;
        this.company = company;
        this.retirement = retirement;
        this.annualSalaries = annualSalaries;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CompanyInfo {
        private String companyId;
        private String companyName;
        private String planType;        // DC/DB/IRP

        @Builder
        private CompanyInfo(String companyId, String companyName, String planType) {
            this.companyId = companyId;
            this.companyName = companyName;
            this.planType = planType;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RetirementInfo {
        private String employeeAccount;
        private String employeeType;    // EXECUTIVE/EMPLOYEE
        private String position;        // 임원/사원
        private LocalDate joinDate;
        private LocalDate startDate;
        private LocalDate effectiveDate;
        private LocalDate terminationDate;
        private Boolean defaultOption;
        private Long balance;
        private String status;          // "재직" / "퇴직"

        @Builder
        private RetirementInfo(
                String employeeAccount,
                String employeeType,
                String position,
                LocalDate joinDate,
                LocalDate startDate,
                LocalDate effectiveDate,
                LocalDate terminationDate,
                Boolean defaultOption,
                Long balance,
                String status
        ) {
            this.employeeAccount = employeeAccount;
            this.employeeType = employeeType;
            this.position = position;
            this.joinDate = joinDate;
            this.startDate = startDate;
            this.effectiveDate = effectiveDate;
            this.terminationDate = terminationDate;
            this.defaultOption = defaultOption;
            this.balance = balance;
            this.status = status;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AnnualSalaryDto {
        private String year;
        private Long salary;

        @Builder
        private AnnualSalaryDto(String year, Long salary) {
            this.year = year;
            this.salary = salary;
        }
    }
}
