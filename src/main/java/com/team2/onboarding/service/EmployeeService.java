package com.team2.onboarding.service;

import com.team2.onboarding.dto.EmployeeDetailResponseDto;
import com.team2.onboarding.dto.EmployeeListItemDto;
import com.team2.onboarding.dto.EmployeeListResponseDto;
import com.team2.onboarding.entity.AnnualSalary;
import com.team2.onboarding.entity.CompanyRetirementDb;
import com.team2.onboarding.entity.CompanyRetirementDc;
import com.team2.onboarding.entity.Contribution;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.EmployeeRetirementDb;
import com.team2.onboarding.entity.EmployeeRetirementDc;
import com.team2.onboarding.enums.EmployeeType;
import com.team2.onboarding.enums.PlanType;
import com.team2.onboarding.repository.AnnualSalaryRepository;
import com.team2.onboarding.repository.CompanyRepository;
import com.team2.onboarding.repository.CompanyRetirementDbRepository;
import com.team2.onboarding.repository.CompanyRetirementDcRepository;
import com.team2.onboarding.repository.ContributionRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.EmployeeRetirementDbRepository;
import com.team2.onboarding.repository.EmployeeRetirementDcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeRetirementDcRepository employeeRetirementDcRepository;
    private final EmployeeRetirementDbRepository employeeRetirementDbRepository;
    private final CompanyRetirementDcRepository companyRetirementDcRepository;
    private final CompanyRetirementDbRepository companyRetirementDbRepository;
    private final AnnualSalaryRepository annualSalaryRepository;
    private final ContributionRepository contributionRepository;

    public EmployeeListResponseDto getEmployees(
            String companyIdStr,
            String name,
            String status,
            int page,
            int size
    ) {
        Long companyId = Long.parseLong(companyIdStr);
        Boolean onlyActive = parseStatus(status);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));

        Page<Employee> employeePage = employeeRepository.searchByCompany(companyId, name, onlyActive, pageable);

        List<Long> employeeIds = employeePage.getContent().stream().map(Employee::getId).toList();
        Map<Long, EmployeeRetirementDc> retirementMap = new HashMap<>();
        if (!employeeIds.isEmpty()) {
            employeeRetirementDcRepository.findByEmployee_Company_Id(companyId)
                    .forEach(r -> retirementMap.put(r.getEmployee().getId(), r));
        }

        String planType = companyRetirementDcRepository.findByCompanyId(companyId)
                .map(CompanyRetirementDc::getPlanType)
                .map(PlanType::name)
                .orElse(null);
        boolean contributionPaidThisMonth = isContributionPaidThisMonth(companyId);

        List<EmployeeListItemDto> items = employeePage.getContent().stream()
                .map(e -> toListItem(e, retirementMap.get(e.getId()), planType, contributionPaidThisMonth))
                .toList();

        return new EmployeeListResponseDto(
                employeePage.getTotalElements(),
                employeePage.getNumber(),
                employeePage.getSize(),
                items
        );
    }

    public EmployeeDetailResponseDto getEmployeeDetail(String companyIdStr, Long employeeId) {
        Long companyId = Long.parseLong(companyIdStr);

        Employee employee = employeeRepository.findByIdAndCompanyId(employeeId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("가입자를 찾을 수 없습니다."));

        EmployeeRetirementDc retirement =
                employeeRetirementDcRepository.findByEmployeeId(employee.getId()).orElse(null);

        String planType = companyRetirementDcRepository.findByCompanyId(companyId)
                .map(CompanyRetirementDc::getPlanType)
                .map(PlanType::name)
                .orElse(null);

        List<EmployeeDetailResponseDto.AnnualSalaryDto> salaries =
                annualSalaryRepository.findByEmployee_IdOrderByYearDesc(employee.getId()).stream()
                        .map(this::toSalaryDto)
                        .toList();

        return EmployeeDetailResponseDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .rrnMasked(maskRrn(employee.getRrn()))
                .company(EmployeeDetailResponseDto.CompanyInfo.builder()
                        .companyName(employee.getCompany().getCompanyName())
                        .planType(planType)
                        .build())
                .retirement(toRetirementInfo(employee, retirement))
                .annualSalaries(salaries)
                .build();
    }

    public EmployeeDetailResponseDto getDbMemberDetail(String companyIdStr, Long employeeId) {
        Long companyId = Long.parseLong(companyIdStr);

        Employee employee = employeeRepository.findByIdAndCompanyId(employeeId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("가입자를 찾을 수 없습니다."));

        EmployeeRetirementDb retirement =
                employeeRetirementDbRepository.findByEmployee_Id(employee.getId()).orElse(null);

        String planType = companyRetirementDbRepository.findByCompany_Id(companyId)
                .map(CompanyRetirementDb::getPlanType)
                .orElse("DB");

        return EmployeeDetailResponseDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .rrnMasked(maskRrn(employee.getRrn()))
                .company(EmployeeDetailResponseDto.CompanyInfo.builder()
                        .companyName(employee.getCompany().getCompanyName())
                        .planType(planType)
                        .build())
                .retirement(toDbRetirementInfo(employee, retirement))
                .annualSalaries(List.of())
                .build();
    }

    private EmployeeDetailResponseDto.RetirementInfo toDbRetirementInfo(Employee e, EmployeeRetirementDb r) {
        if (r == null) return null;
        EmployeeType type = e.getEmployeeType();
        return EmployeeDetailResponseDto.RetirementInfo.builder()
                .employeeAccount(r.getEmployeeAccount())
                .employeeType(type != null ? type.name() : null)
                .position(type != null ? type.getDescription() : null)
                .joinDate(r.getJoinDate())
                .startDate(e.getStartDate())
                .terminationDate(e.getTerminationDate())
                .defaultOption(null)
                .balance(null)
                .status(e.getTerminationDate() == null ? "재직" : "퇴직")
                .build();
    }

    private EmployeeListItemDto toListItem(
            Employee e,
            EmployeeRetirementDc r,
            String planType,
            boolean contributionPaid
    ) {
        EmployeeType type = e.getEmployeeType();
        return EmployeeListItemDto.builder()
                .id(e.getId())
                .name(e.getName())
                .position(type != null ? type.getDescription() : null)
                .joinDate(r != null ? r.getJoinDate() : null)
                .planType(planType)
                .balance(null)
                .contributionPaid(contributionPaid)
                .status(e.getTerminationDate() == null ? "재직" : "퇴직")
                .build();
    }

    private EmployeeDetailResponseDto.RetirementInfo toRetirementInfo(Employee e, EmployeeRetirementDc r) {
        if (r == null) return null;
        EmployeeType type = e.getEmployeeType();
        return EmployeeDetailResponseDto.RetirementInfo.builder()
                .employeeAccount(r.getEmployeeAccount())
                .employeeType(type != null ? type.name() : null)
                .position(type != null ? type.getDescription() : null)
                .joinDate(r.getJoinDate())
                .startDate(e.getStartDate())
                .effectiveDate(r.getEffectiveDate())
                .terminationDate(e.getTerminationDate())
                .defaultOption(r.getDefaultOption())
                .balance(null)
                .status(e.getTerminationDate() == null ? "재직" : "퇴직")
                .build();
    }

    private EmployeeDetailResponseDto.AnnualSalaryDto toSalaryDto(AnnualSalary s) {
        return EmployeeDetailResponseDto.AnnualSalaryDto.builder()
                .year(s.getYear())
                .salary(s.getSalary())
                .minContribution(s.getMinContribution())
                .contribution(s.getContribution())
                .build();
    }

    private Boolean parseStatus(String status) {
        if (status == null || status.isBlank()) return null;
        return switch (status.toUpperCase()) {
            case "ACTIVE" -> Boolean.TRUE;
            case "RETIRED" -> Boolean.FALSE;
            default -> throw new IllegalArgumentException("status 는 ACTIVE 또는 RETIRED 여야 합니다.");
        };
    }

    private boolean isContributionPaidThisMonth(Long companyId) {
        CompanyRetirementDc crd = companyRetirementDcRepository.findByCompanyId(companyId).orElse(null);
        if (crd == null) return false;
        return contributionRepository.findByCompanyRetirementDc(crd).stream()
                .anyMatch(c -> c.getPaidDate() != null
                        && YearMonth.from(c.getPaidDate()).equals(YearMonth.now()));
    }

    private String maskRrn(String rrn) {
        if (rrn == null || rrn.length() < 7) return rrn;
        return rrn.substring(0, 6) + "-*******";
    }
}
