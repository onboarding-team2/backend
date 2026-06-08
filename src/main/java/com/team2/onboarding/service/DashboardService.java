package com.team2.onboarding.service;

import com.team2.onboarding.dto.DcContributionStatusResponseDto;
import com.team2.onboarding.dto.ExpectedRetireeDto;
import com.team2.onboarding.dto.MonthlyPaymentDto;
import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.CompanyRetirementDc;
import com.team2.onboarding.entity.Contribution;
import com.team2.onboarding.entity.EmployeeRetirementDc;
import com.team2.onboarding.repository.CompanyRepository;
import com.team2.onboarding.repository.CompanyRetirementDcRepository;
import com.team2.onboarding.repository.ContributionRepository;
import com.team2.onboarding.repository.EmployeeRetirementDcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CompanyRepository companyRepository;
    private final CompanyRetirementDcRepository companyRetirementDcRepository;
    private final ContributionRepository contributionRepository;
    private final EmployeeRetirementDcRepository employeeRetirementDcRepository;

    public DcContributionStatusResponseDto getDcContributionStatus(String companyId) {
        Long id = Long.parseLong(companyId);
        companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));

        CompanyRetirementDc crd = companyRetirementDcRepository.findByCompanyId(id)
                .orElseThrow(() -> new IllegalArgumentException("퇴직연금 정보가 없습니다."));

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        List<Contribution> contributions = contributionRepository.findByCompanyRetirementDcAndDueDateBetween(
                crd,
                LocalDate.of(currentYear, 1, 1),
                LocalDate.of(currentYear, 12, 31)
        );

        Map<Integer, Long> paidByMonth = contributions.stream()
                .filter(c -> c.getPaidDate() != null)
                .collect(Collectors.toMap(
                        c -> c.getDueDate().getMonthValue(),
                        Contribution::getContributionAmount
                ));

        long expectedAmount = (long) contributions.stream()
                .mapToLong(Contribution::getContributionAmount)
                .average()
                .orElse(0);

        List<MonthlyPaymentDto> payments = new ArrayList<>();
        for (int month = 1; month <= currentMonth; month++) {
            Long amount = paidByMonth.get(month);
            payments.add(MonthlyPaymentDto.builder()
                    .month(month)
                    .amount(amount)
                    .paid(amount != null)
                    .build());
        }

        return DcContributionStatusResponseDto.builder()
                .year(currentYear)
                .expectedAmount(expectedAmount)
                .payments(payments)
                .build();
    }

    public List<ExpectedRetireeDto> getExpectedRetirees(String companyId) {
        Long id = Long.parseLong(companyId);
        companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));

        List<EmployeeRetirementDc> retirees = employeeRetirementDcRepository
                .findByEmployee_Company_IdAndTerminationDateAfterOrderByTerminationDate(id, LocalDate.now());

        List<ExpectedRetireeDto> result = new ArrayList<>();
        for (int i = 0; i < retirees.size(); i++) {
            EmployeeRetirementDc er = retirees.get(i);
            result.add(ExpectedRetireeDto.builder()
                    .rank(i + 1)
                    .name(er.getEmployee().getName())
                    .retirementDate(er.getTerminationDate())
                    .retirementType(null)
                    .build());
        }
        return result;
    }
}
