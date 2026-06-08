package com.team2.onboarding.service;

import com.team2.onboarding.dto.ExpectedRetireeDto;
import com.team2.onboarding.entity.EmployeeRetirementDc;
import com.team2.onboarding.repository.CompanyRepository;
import com.team2.onboarding.repository.EmployeeRetirementDcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CompanyRepository companyRepository;
    private final EmployeeRetirementDcRepository employeeRetirementDcRepository;

    public List<ExpectedRetireeDto> getExpectedRetirees(String companyId) {
        Long id = Long.parseLong(companyId);
        companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));

        List<EmployeeRetirementDc> retirees = employeeRetirementDcRepository
                .findByEmployee_Company_IdAndEmployee_TerminationDateAfterOrderByEmployee_TerminationDateAsc(id, LocalDate.now());

        List<ExpectedRetireeDto> result = new ArrayList<>();
        for (int i = 0; i < retirees.size(); i++) {
            EmployeeRetirementDc er = retirees.get(i);
            result.add(ExpectedRetireeDto.builder()
                    .rank(i + 1)
                    .name(er.getEmployee().getName())
                    .retirementDate(er.getEmployee().getTerminationDate())
                    .retirementType(null)
                    .build());
        }
        return result;
    }
}
