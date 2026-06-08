package com.team2.onboarding.service;

import com.team2.onboarding.entity.CompanyRetirementDc;
import com.team2.onboarding.repository.CompanyRetirementDcRepository;
import com.team2.onboarding.repository.ContributionRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.EmployeeRetirementDcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRetirementDcRepository companyRetirementDcRepository;
    private final ContributionRepository contributionRepository;
    private final EmployeeRetirementDcRepository employeeRetirementDcRepository;

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
