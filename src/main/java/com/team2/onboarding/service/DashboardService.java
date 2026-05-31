package com.team2.onboarding.service;

import com.team2.onboarding.dto.DcContributionStatusResponseDto;
import com.team2.onboarding.dto.ExpectedRetireeDto;
import com.team2.onboarding.dto.MonthlyPaymentDto;
import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.Contribution;
import com.team2.onboarding.entity.EmployeeRetirement;
import com.team2.onboarding.repository.CompanyRepository;
import com.team2.onboarding.repository.ContributionRepository;
import com.team2.onboarding.repository.EmployeeRetirementRepository;
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
    private final ContributionRepository contributionRepository;
    private final EmployeeRetirementRepository employeeRetirementRepository;

    public DcContributionStatusResponseDto getDcContributionStatus(String companyId) {
        Company company = companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        List<Contribution> contributions = contributionRepository.findByCompanyAndPaidDateBetween(
                company,
                LocalDate.of(currentYear, 1, 1),
                LocalDate.of(currentYear, 12, 31)
        );

        Map<Integer, Long> paidByMonth = contributions.stream()
                .collect(Collectors.toMap(
                        c -> c.getPaidDate().getMonthValue(),
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
        Company company = companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));

        List<EmployeeRetirement> retirees = employeeRetirementRepository
                .findByEmployee_CompanyAndTerminationDateAfterOrderByTerminationDate(company, LocalDate.now());

        List<ExpectedRetireeDto> result = new ArrayList<>();
        for (int i = 0; i < retirees.size(); i++) {
            EmployeeRetirement er = retirees.get(i);
            result.add(ExpectedRetireeDto.builder()
                    .rank(i + 1)
                    .name(er.getEmployee().getName())
                    .memberId(er.getEmployee().getMemberId())
                    .retirementDate(er.getTerminationDate())
                    .retirementType(er.getRetirementType() != null
                            ? er.getRetirementType().getDescription()
                            : null)
                    .build());
        }
        return result;
    }
}
