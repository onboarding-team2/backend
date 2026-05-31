package com.team2.onboarding.service;

import com.team2.onboarding.entity.Contribution;
import com.team2.onboarding.repository.ContributionRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final EmployeeRepository employeeRepository;
    private final ContributionRepository contributionRepository;

    public long getEmployeeCount(Long companyId) {
        return employeeRepository.countByCompany_Id(companyId);
    }

    public Long getDcAmount(Long companyId) {

        Contribution contribution = contributionRepository
                .findByCompany_Id(companyId)
                .orElseThrow(() -> new RuntimeException("적립금 정보 없음"));

        return contribution.getContributionAmount();
    }
}
