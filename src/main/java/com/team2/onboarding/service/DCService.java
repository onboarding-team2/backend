package com.team2.onboarding.service;

import com.team2.onboarding.dto.DCDashboardResponseDto;
import com.team2.onboarding.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DCService {

    private final EmployeeRepository employeeRepository;

    // TODO: DC 대시보드
    // public Object getDashboard(String companyId) {
    //     return null;
    // }

    // DC 대시보드
    public DCDashboardResponseDto getDashboard(String companyId) {

        long totalEmployee =
                employeeRepository.countByCompany_CompanyId(companyId);

        return DCDashboardResponseDto.builder()
                .totalEmployee(totalEmployee)
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
