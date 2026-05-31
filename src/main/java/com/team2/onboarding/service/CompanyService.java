package com.team2.onboarding.service;

import com.team2.onboarding.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final EmployeeRepository employeeRepository;

    public long getEmployeeCount(Long companyId) {
        return employeeRepository.countByCompany_Id(companyId);
    }
}
