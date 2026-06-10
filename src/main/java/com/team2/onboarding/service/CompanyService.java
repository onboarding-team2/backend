package com.team2.onboarding.service;

import com.team2.onboarding.dto.CompanyInfoDto;
import com.team2.onboarding.dto.DcContributionStatusResponseDto;
import com.team2.onboarding.dto.MonthlyPaymentDto;
import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.CompanyRetirementDc;
import com.team2.onboarding.entity.Contribution;
import com.team2.onboarding.enums.PlanType;
import com.team2.onboarding.repository.CompanyRepository;
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
