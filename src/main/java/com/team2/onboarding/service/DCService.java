package com.team2.onboarding.service;

import com.team2.onboarding.dto.DCDashboardResponseDto;
import com.team2.onboarding.dto.DefaultOptionMemberDto;
import com.team2.onboarding.dto.DcMemberItemDto;
import com.team2.onboarding.entity.CompanyRetirementDc;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.EmployeeRetirementDc;
import com.team2.onboarding.enums.EmployeeType;
import com.team2.onboarding.repository.CompanyRetirementDcRepository;
import com.team2.onboarding.repository.ContributionRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.EmployeeRetirementDcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
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

    public DCDashboardResponseDto getDashboard(String companyId) {
        Long id = Long.parseLong(companyId);

        long totalBalance = contributionRepository.sumPaidContributionByCompanyId(id);;

        long totalEmployee = employeeRepository.countByCompany_Id(id);

        long defaultOptionNotSelected = employeeRetirementDcRepository
                .countByEmployee_Company_IdAndDefaultOption(id, "N");

        long irpAccountNotOpened = employeeRetirementDcRepository
                .countByEmployee_Company_IdAndHasIrpAccount(id, "N");

        CompanyRetirementDc crd = companyRetirementDcRepository.findByCompanyId(id).orElse(null);

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

        return DCDashboardResponseDto.builder()
                .totalBalance(totalBalance)
                .totalEmployee(totalEmployee)
                .defaultOptionNotSelected(defaultOptionNotSelected)
                .defaultOptionMembers(defaultOptionMembers)
                .defaultOptionSummary(defaultOptionSummary)
                .irpAccountNotOpened(irpAccountNotOpened)
                .thisMonthContribution(thisMonthContribution)
                .contributionDueDate(contributionDueDate)
                .build();
    }

    public List<DcMemberItemDto> getMembers(String companyId) {
        return getMembers(companyId, null);
    }

    public List<DcMemberItemDto> getMembers(String companyId, String filter) {
        Long id = Long.parseLong(companyId);

        List<Employee> employees = employeeRepository.findByCompany_Id(id);

        Map<Long, EmployeeRetirementDc> retirementMap = employeeRetirementDcRepository
                .findByEmployee_Company_Id(id)
                .stream()
                .collect(Collectors.toMap(r -> r.getEmployee().getId(), r -> r));

        return employees.stream()
                .map(e -> {
                    EmployeeRetirementDc erd = retirementMap.get(e.getId());
                    EmployeeType type = e.getEmployeeType();
                    return DcMemberItemDto.builder()
                            .id(e.getId())
                            .name(e.getName())
                            .rrnMasked(maskRrn(e.getRrn()))
                            .position(type != null ? type.getDescription() : null)
                            .startDate(e.getStartDate())
                            .joinDate(erd != null ? erd.getJoinDate() : null)
                            .hasIrpAccount(erd != null ? erd.getHasIrpAccount() : null)
                            .defaultOption(erd != null ? erd.getDefaultOption() : null)
                            .balance(null)
                            .contributionPaid(null)
                            .status(e.getTerminationDate() != null ? "퇴직" : "재직")
                            .build();
                })
                .filter(dto -> matchesFilter(dto, filter))
                .toList();
    }

    /**
     * 미처리 현황용 필터.
     * - default-unset: 디폴트옵션 미선정(Y 아님)
     * - irp-none: IRP 개설 미완료(Y 아님)
     */
    private boolean matchesFilter(DcMemberItemDto dto, String filter) {
        if (filter == null || filter.isBlank()) return true;
        return switch (filter) {
            case "default-unset" -> !"Y".equals(dto.getDefaultOption());
            case "irp-none" -> !"Y".equals(dto.getHasIrpAccount());
            default -> true;
        };
    }

    private String maskRrn(String rrn) {
        if (rrn == null || rrn.length() < 7) return rrn;
        return rrn.substring(0, 6) + "-*******";
    }

    public Object getDeadlines(String companyId) {
        return null;
    }

    public Object getDocuments(String companyId) {
        return null;
    }
}
