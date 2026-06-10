package com.team2.onboarding.service;

import com.team2.onboarding.dto.DcContributionStatusResponseDto;
import com.team2.onboarding.dto.MonthlyPaymentDto;
import com.team2.onboarding.entity.CompanyRetirementDc;
import com.team2.onboarding.entity.Contribution;
import com.team2.onboarding.repository.CompanyRetirementDcRepository;
import com.team2.onboarding.repository.ContributionRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.EmployeeRetirementDcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

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

    public DcContributionStatusResponseDto getDcContributions(String companyId) {
        Long id = Long.parseLong(companyId);
        CompanyRetirementDc crd = companyRetirementDcRepository.findByCompanyId(id)
                .orElseThrow(() -> new RuntimeException("DC 퇴직연금 정보 없음"));

        int year = LocalDate.now().getYear();
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<Contribution> contributions = contributionRepository
                .findByCompanyRetirementDcAndDueDateBetween(crd, start, end);

        long expectedAmount = contributions.stream()
                .mapToLong(Contribution::getContributionAmount)
                .max()
                .orElse(0L);

        List<MonthlyPaymentDto> payments = contributions.stream()
                .sorted(Comparator.comparing(Contribution::getDueDate))
                .map(c -> MonthlyPaymentDto.builder()
                        .month(c.getDueDate().getMonthValue())
                        .amount(c.getContributionAmount())
                        .paid(c.getPaidDate() != null)
                        .build())
                .toList();

        return DcContributionStatusResponseDto.builder()
                .year(year)
                .expectedAmount(expectedAmount)
                .payments(payments)
                .build();
    }
}
