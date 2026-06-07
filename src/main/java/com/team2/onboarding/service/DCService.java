package com.team2.onboarding.service;

import com.team2.onboarding.dto.DCDashboardResponseDto;
import com.team2.onboarding.repository.ContributionRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.EmployeeRetirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DCService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeRetirementRepository employeeRetirementRepository;
    private final ContributionRepository contributionRepository;

    // TODO: DC 대시보드
    // public Object getDashboard(String companyId) {
    //     return null;
    // }

    // DC 대시보드
    public DCDashboardResponseDto getDashboard(String companyId) {

        // 총 가입자 수
        long totalEmployee =
                employeeRepository.countByCompany_CompanyId(companyId);

        // 디폴트옵션 미지정자 수
        long defaultOptionNotSelected =
                employeeRetirementRepository
                        .countByEmployee_Company_CompanyIdAndDefaultOption(
                                companyId,
                                false
                        );

        // DC 적립금 총액
        long totalContributionAmount =
                contributionRepository
                        .sumContributionAmountByCompanyId(companyId);

        return DCDashboardResponseDto.builder()
                .totalEmployee(totalEmployee)
                .defaultOptionNotSelected(defaultOptionNotSelected)
                .totalContributionAmount(totalContributionAmount)
                .build();
    }


    // TODO: DC 가입자 현황
    public Object getMembers(String companyId) {
        return null;
    }

    // TODO: DC 만기/퇴직 예정
    public Object getDeadlines(String companyId) {
        return null;
    }

    // TODO: DC 증명서류
    public Object getDocuments(String companyId) {
        return null;
    }
}
