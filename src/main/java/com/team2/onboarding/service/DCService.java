package com.team2.onboarding.service;

import com.team2.onboarding.dto.DCDashboardResponseDto;
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

import java.time.LocalDate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DCService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeRetirementDcRepository employeeRetirementDcRepository;
    private final CompanyRetirementDcRepository companyRetirementDcRepository;
    private final ContributionRepository contributionRepository;

    public DCDashboardResponseDto getDashboard(String companyId) {
        Long id = Long.parseLong(companyId);

        long totalBalance = employeeRetirementDcRepository.sumBalanceByCompanyId(id);

        long totalEmployee = employeeRepository.countByCompany_Id(id);

        long defaultOptionNotSelected = employeeRetirementDcRepository
                .countByEmployee_Company_IdAndDefaultOptionIsNull(id);

        CompanyRetirementDc crd = companyRetirementDcRepository.findByCompanyId(id).orElse(null);

        long thisMonthContribution = 0L;
        String contributionDueDate = null;
        if (crd != null) {
            LocalDate now = LocalDate.now();
            LocalDate start = now.withDayOfMonth(1);
            LocalDate end = now.withDayOfMonth(now.lengthOfMonth());
            var thisMonth = contributionRepository
                    .findTopByCompanyRetirementDcAndDueDateBetweenOrderByDueDateAsc(crd, start, end);
            if (thisMonth.isPresent()) {
                thisMonthContribution = thisMonth.get().getContributionAmount();
                contributionDueDate = thisMonth.get().getDueDate().toString();
            }
        }

        return DCDashboardResponseDto.builder()
                .totalBalance(totalBalance)
                .totalEmployee(totalEmployee)
                .defaultOptionNotSelected(defaultOptionNotSelected)
                .thisMonthContribution(thisMonthContribution)
                .contributionDueDate(contributionDueDate)
                .build();
    }

    public List<DcMemberItemDto> getMembers(String companyId) {
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
                            .position(type != null ? type.getDescription() : null)
                            .startDate(e.getStartDate())
                            .balance(erd != null ? erd.getBalance() : null)
                            .contributionPaid(null)
                            .status(e.getTerminationDate() != null ? "퇴직" : "재직")
                            .build();
                })
                .toList();
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
