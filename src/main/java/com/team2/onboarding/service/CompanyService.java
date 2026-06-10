package com.team2.onboarding.service;

import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.CompanyRetirementDc;
import com.team2.onboarding.enums.PlanType;
import com.team2.onboarding.repository.*;
import com.team2.onboarding.dto.CompanyInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRetirementDcRepository companyRetirementDcRepository;
    private final ContributionRepository contributionRepository;
    private final EmployeeRetirementDcRepository employeeRetirementDcRepository;
    private final CompanyRepository companyRepository;

    public CompanyInfoDto getCompanyInfo(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("해당 기업 정보가 존재하지 않습니다."));


        PlanType planType = company.getPlanType();

        return CompanyInfoDto.builder()
                .companyName(company.getCompanyName())
                .businessNumber(formatBrn(company.getBrn()))
                .planType(planType)
                .build();
    }

    private String formatBrn(String brn) {
        if (brn != null && brn.length() == 10) {
            return brn.substring(0, 3) + "-" + brn.substring(3, 5) + "-" + brn.substring(5);
        }
        return brn;
    }

    public long getEmployeeCount(Long companyId) {
        return employeeRepository.countByCompany_Id(companyId);
    }

    public Long getDcAmount(Long companyId) {
        CompanyRetirementDc crd = companyRetirementDcRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("적립금 정보 없음"));

        return contributionRepository.findByCompanyRetirementDc(crd).stream()
                .filter(c -> c.getPaidDate() != null)
                .mapToLong(c -> c.getContributionAmount())
                .sum();
    }

    public long getDefaultOptionNonEmployeeCount(Long companyId) {
        return employeeRetirementDcRepository.countByEmployee_Company_IdAndDefaultOption(companyId, "N");
    }
}
