package com.team2.onboarding.service;

import com.team2.onboarding.dto.DCDashboardResponseDto;
import com.team2.onboarding.dto.DefaultOptionMemberDto;
import com.team2.onboarding.dto.DcMemberItemDto;
import com.team2.onboarding.dto.PageResponse;
import com.team2.onboarding.entity.CompanyRetirementDc;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.EmployeeRetirementDc;
import com.team2.onboarding.enums.EmployeeType;
import com.team2.onboarding.enums.PaymentCycle;
import com.team2.onboarding.repository.AnnualSalaryRepository;
import com.team2.onboarding.repository.CompanyRetirementDcRepository;
import com.team2.onboarding.repository.ContributionRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.EmployeeRetirementDcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DCService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeRetirementDcRepository employeeRetirementDcRepository;
    private final CompanyRetirementDcRepository companyRetirementDcRepository;
    private final ContributionRepository contributionRepository;
    private final AnnualSalaryRepository annualSalaryRepository;

    public DCDashboardResponseDto getDashboard(String companyId) {
        Long id = Long.parseLong(companyId);

        CompanyRetirementDc crdForCycle = companyRetirementDcRepository.findByCompanyId(id).orElse(null);
        PaymentCycle cycle = crdForCycle != null ? crdForCycle.getPaymentCycle() : null;

        long sum2025 = annualSalaryRepository.sumContributionByCompanyAndYear(id, "2025");
        long sum2026 = annualSalaryRepository.sumContributionByCompanyAndYear(id, "2026");

        long partial2026;
        if (cycle == PaymentCycle.MONTHLY) {
            int currentMonth = LocalDate.now().getMonthValue();
            partial2026 = Math.round(sum2026 * (currentMonth - 1.0) / 12.0);
        } else if (cycle == PaymentCycle.QUARTERLY) {
            partial2026 = Math.round(sum2026 / 4.0);
        } else {
            partial2026 = 0L;
        }

        long totalBalance = sum2025 + partial2026;

        long totalEmployee = employeeRepository.countByCompany_Id(id);

        long defaultOptionNotSelected = employeeRetirementDcRepository
                .countByEmployee_Company_IdAndDefaultOption(id, "N");

        long irpAccountNotOpened = employeeRetirementDcRepository
                .countByEmployee_Company_IdAndHasIrpAccount(id, "N");

        CompanyRetirementDc crd = crdForCycle;

        long thisMonthContribution = 0L;
        String contributionDueDate = null;
//        if (crd != null) {
//            LocalDate now = LocalDate.now();
//            LocalDate start = now.withDayOfMonth(1);
//            LocalDate end = now.withDayOfMonth(now.lengthOfMonth());
//            var thisMonth = contributionRepository
//                    .findTopByCompanyRetirementDcAndDueDateBetweenOrderByDueDateAsc(crd, start, end);
//            if (thisMonth.isPresent()) {
//                thisMonthContribution = thisMonth.get().getContributionAmount();
//                contributionDueDate = thisMonth.get().getDueDate().toString();
//            }
//        }
        LocalDate now = LocalDate.now();
        var nextContribution = contributionRepository
                .findTopByCompanyRetirementDcAndDueDateGreaterThanEqualOrderByDueDateAsc(crd, now);

        if (nextContribution.isPresent()) {
            thisMonthContribution = nextContribution.get().getContributionAmount();
            contributionDueDate = nextContribution.get().getDueDate().toString();
        }

        List<EmployeeRetirementDc> notSelectedList = employeeRetirementDcRepository
                .findByEmployee_Company_IdAndDefaultOptionOrderByJoinDateAsc(id, "N");

        LocalDate today = LocalDate.now();
        List<DefaultOptionMemberDto> defaultOptionMembers = notSelectedList.stream()
                .map(er -> DefaultOptionMemberDto.builder()
                        .name(er.getEmployee().getName())
                        .joinDate(er.getJoinDate())
                        .daysElapsed(ChronoUnit.DAYS.between(er.getJoinDate(), today))
                        .build())
                .toList();

        String defaultOptionSummary = null;
        if (defaultOptionNotSelected >= 3) {
            String firstName = defaultOptionMembers.get(0).getName();
            defaultOptionSummary = firstName + " 외 " + (defaultOptionNotSelected - 1) + "명";
        }

        PaymentCycle paymentCycle = cycle;

        return DCDashboardResponseDto.builder()
                .totalBalance(totalBalance)
                .totalEmployee(totalEmployee)
                .defaultOptionNotSelected(defaultOptionNotSelected)
                .defaultOptionMembers(defaultOptionMembers)
                .defaultOptionSummary(defaultOptionSummary)
                .irpAccountNotOpened(irpAccountNotOpened)
                .thisMonthContribution(thisMonthContribution)
                .contributionDueDate(contributionDueDate)
                .paymentCycle(paymentCycle)
                .build();
    }

    /**
     * 가입자 목록 조회(서버 필터링 + 페이지네이션).
     * 필터 값은 프론트 칩과 동일한 한글값(재직/사원/보유/선정/납입완료 등)을 사용한다.
     * 같은 카테고리 내 다중값은 OR, 카테고리 간에는 AND.
     */
    public PageResponse<DcMemberItemDto> getMembers(
            String companyId,
            String name,
            List<String> status,
            List<String> type,
            List<String> irp,
            List<String> defaultOption,
            List<String> contribution,
            int page,
            int size
    ) {
        Long id = Long.parseLong(companyId);

        List<Employee> employees = employeeRepository.findByCompany_Id(id);

        Map<Long, EmployeeRetirementDc> retirementMap = employeeRetirementDcRepository
                .findByEmployee_Company_Id(id)
                .stream()
                .collect(Collectors.toMap(r -> r.getEmployee().getId(), r -> r));

        Collator collator = Collator.getInstance(Locale.KOREAN);

        List<DcMemberItemDto> all = employees.stream()
                .map(e -> {
                    EmployeeRetirementDc erd = retirementMap.get(e.getId());
                    EmployeeType empType = e.getEmployeeType();
                    return DcMemberItemDto.builder()
                            .id(e.getId())
                            .name(e.getName())
                            .rrnMasked(maskRrn(e.getRrn()))
                            .position(empType != null ? empType.getDescription() : null)
                            .startDate(e.getStartDate())
                            .joinDate(erd != null ? erd.getJoinDate() : null)
                            .hasIrpAccount(erd != null ? erd.getHasIrpAccount() : null)
                            .defaultOption(erd != null ? erd.getDefaultOption() : null)
                            .balance(null)
                            .contributionPaid(null)
                            .status(e.getTerminationDate() != null ? "퇴직" : "재직")
                            .build();
                })
                .filter(dto -> matches(dto, name, status, type, irp, defaultOption, contribution))
                .sorted((a, b) -> collator.compare(a.getName(), b.getName()))
                .toList();

        return PageResponse.of(all, page, size);
    }

    private boolean matches(
            DcMemberItemDto dto,
            String name,
            List<String> status,
            List<String> type,
            List<String> irp,
            List<String> defaultOption,
            List<String> contribution
    ) {
        if (name != null && !name.isBlank() && (dto.getName() == null || !dto.getName().contains(name))) return false;
        if (notIn(status, dto.getStatus())) return false;
        if (notIn(type, dto.getPosition())) return false;
        if (notIn(irp, "Y".equals(dto.getHasIrpAccount()) ? "보유" : "미보유")) return false;
        if (notIn(defaultOption, "Y".equals(dto.getDefaultOption()) ? "선정" : "미선정")) return false;
        if (notIn(contribution, Boolean.TRUE.equals(dto.getContributionPaid()) ? "납입완료" : "미납")) return false;
        return true;
    }

    /** 필터가 지정돼 있고(비어있지 않고) 값이 거기 포함되지 않으면 true(=제외). */
    private boolean notIn(List<String> filter, String value) {
        return filter != null && !filter.isEmpty() && !filter.contains(value);
    }

    private String maskRrn(String rrn) {
        if (rrn == null || rrn.length() < 7) return rrn;
        return rrn.substring(0, 6) + "-*******";
    }

    public Object getSchedules(String companyId) {
        return null;
    }

    public Object getDocuments(String companyId) {
        return null;
    }
}
