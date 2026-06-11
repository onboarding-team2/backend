package com.team2.onboarding.service;

import com.team2.onboarding.dto.CompanyInfoDto;
import com.team2.onboarding.dto.ContributionChartItemDto;
import com.team2.onboarding.dto.DcContributionStatusResponseDto;
import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.CompanyRetirementDc;
import com.team2.onboarding.entity.Contribution;
import com.team2.onboarding.enums.PaymentCycle;
import com.team2.onboarding.enums.PlanType;
import com.team2.onboarding.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

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

        PaymentCycle cycle = crd.getPaymentCycle();
        LocalDate now = LocalDate.now();
        List<ContributionChartItemDto> items;

        if (cycle == PaymentCycle.MONTHLY) {
            items = IntStream.range(0, 4)
                    .mapToObj(i -> now.minusMonths(3 - i))
                    .map(month -> buildChartItem(
                            month.getMonthValue() + "월",
                            crd,
                            month.withDayOfMonth(1),
                            month.withDayOfMonth(month.lengthOfMonth())))
                    .toList();
        } else if (cycle == PaymentCycle.QUARTERLY) {
            int year = now.getYear();
            items = IntStream.rangeClosed(1, 4)
                    .mapToObj(q -> {
                        LocalDate start = LocalDate.of(year, (q - 1) * 3 + 1, 1);
                        return buildChartItem(q + "분기", crd, start, start.plusMonths(3).minusDays(1));
                    })
                    .toList();
        } else {
            int currentYear = now.getYear();
            items = IntStream.range(0, 3)
                    .mapToObj(i -> currentYear - 2 + i)
                    .map(year -> buildChartItem(
                            String.valueOf(year),
                            crd,
                            LocalDate.of(year, 1, 1),
                            LocalDate.of(year, 12, 31)))
                    .toList();
        }

        return DcContributionStatusResponseDto.builder()
                .cycle(cycle.name())
                .items(items)
                .build();
    }

    private ContributionChartItemDto buildChartItem(String label, CompanyRetirementDc crd, LocalDate start, LocalDate end) {
        List<Contribution> list = contributionRepository.findByCompanyRetirementDcAndDueDateBetween(crd, start, end);
        long paidAmount = list.stream()
                .filter(c -> "납입완료".equals(c.getStatus()))
                .mapToLong(Contribution::getContributionAmount)
                .sum();
        if (paidAmount > 0) {
            return ContributionChartItemDto.builder().label(label).amount(paidAmount).paid(true).build();
        }
        long expectedAmount = list.stream()
                .mapToLong(Contribution::getContributionAmount)
                .findFirst()
                .orElse(0L);
        return ContributionChartItemDto.builder().label(label).amount(expectedAmount).paid(false).build();
    }
}
