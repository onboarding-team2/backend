package com.team2.onboarding.service;

import com.team2.onboarding.entity.Contribution;
import com.team2.onboarding.repository.ContributionRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.EmployeeRetirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final EmployeeRepository employeeRepository;
    private final ContributionRepository contributionRepository;
    private final EmployeeRetirementRepository employeeRetirementRepository;

    // 회사 별 재직 중인 직원 수
    public long getEmployeeCount(Long companyId) {
        return employeeRepository.countByCompany_Id(companyId);
    }

    // 회사 별 DC형 적립 총액
    public Long getDcAmount(Long companyId) {

        Contribution contribution = contributionRepository
                .findByCompany_Id(companyId)
                .orElseThrow(() -> new RuntimeException("적립금 정보 없음"));

        return contribution.getContributionAmount();
    }

    // DC형 디폴트옵션 미지정자 수 구하기
    public long getDefaultOptionNonEmployeeCount(Long companyId) {
        return employeeRetirementRepository
                .countByEmployee_Company_IdAndDefaultOptionFalse(companyId);
    }
}
