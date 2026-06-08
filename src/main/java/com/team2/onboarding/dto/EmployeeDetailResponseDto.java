package com.team2.onboarding.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeDetailResponseDto {

    private Long id;
    private String name;
    private String rrnMasked;
    private CompanyInfo company;
    private RetirementInfo retirement;
    private List<AnnualSalaryDto> annualSalaries;

    @Builder
    private EmployeeDetailResponseDto(
            Long id,
            String name,
            String rrnMasked,
            CompanyInfo company,
            RetirementInfo retirement,
            List<AnnualSalaryDto> annualSalaries
    ) {
        this.id = id;
        this.name = name;
        this.rrnMasked = rrnMasked;
        this.company = company;
        this.retirement = retirement;
        this.annualSalaries = annualSalaries;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CompanyInfo {
        private String companyName;
        private String planType;

        @Builder
        private CompanyInfo(String companyName, String planType) {
            this.companyName = companyName;
            this.planType = planType;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RetirementInfo {
        private String employeeAccount;
        private String employeeType;
        private String position;
        private LocalDate joinDate;
        private LocalDate startDate;
        private LocalDate effectiveDate;
        private LocalDate terminationDate;
        private String defaultOption;
        private Long balance;
        private String status;

        @Builder
        private RetirementInfo(
                String employeeAccount,
                String employeeType,
                String position,
                LocalDate joinDate,
                LocalDate startDate,
                LocalDate effectiveDate,
                LocalDate terminationDate,
                String defaultOption,
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
