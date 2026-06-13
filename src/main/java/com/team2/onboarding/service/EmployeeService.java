package com.team2.onboarding.service;

import com.team2.onboarding.dto.EmployeeDetailResponseDto;
import com.team2.onboarding.entity.AnnualSalary;
import com.team2.onboarding.entity.AnnualSalaryDb;
import com.team2.onboarding.entity.CompanyRetirementDc;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.EmployeeRetirementDb;
import com.team2.onboarding.entity.EmployeeRetirementDc;
import com.team2.onboarding.enums.EmployeeType;
import com.team2.onboarding.enums.PlanType;
import com.team2.onboarding.repository.AnnualSalaryDbRepository;
import com.team2.onboarding.repository.AnnualSalaryRepository;
import com.team2.onboarding.repository.CompanyRetirementDbRepository;
import com.team2.onboarding.repository.CompanyRetirementDcRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.EmployeeRetirementDbRepository;
import com.team2.onboarding.repository.EmployeeRetirementDcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeRetirementDcRepository employeeRetirementDcRepository;
    private final EmployeeRetirementDbRepository employeeRetirementDbRepository;
    private final CompanyRetirementDcRepository companyRetirementDcRepository;
    private final CompanyRetirementDbRepository companyRetirementDbRepository;
    private final AnnualSalaryRepository annualSalaryRepository;
    private final AnnualSalaryDbRepository annualSalaryDbRepository;

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
                .map(crdb -> crdb.getPlanType() != null ? crdb.getPlanType().name() : "DB")
                .orElse("DB");

        List<EmployeeDetailResponseDto.AnnualSalaryDto> salaries =
                annualSalaryDbRepository.findByEmployee_IdOrderByYearDesc(employee.getId()).stream()
                        .map(this::toDbSalaryDto)
                        .toList();

        return EmployeeDetailResponseDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .rrnMasked(maskRrn(employee.getRrn()))
                .company(EmployeeDetailResponseDto.CompanyInfo.builder()
                        .companyName(employee.getCompany().getCompanyName())
                        .planType(planType)
                        .build())
                .retirement(toDbRetirementInfo(employee, retirement))
                .annualSalaries(salaries)
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
                .hasIrpAccount(r.getHasIrpAccount())
                .balance(null)
                .status(e.getTerminationDate() == null ? "재직" : "퇴직")
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
                .hasIrpAccount(r.getHasIrpAccount())
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

    private EmployeeDetailResponseDto.AnnualSalaryDto toDbSalaryDto(AnnualSalaryDb s) {
        return EmployeeDetailResponseDto.AnnualSalaryDto.builder()
                .year(s.getYear())
                .salary(s.getSalary())
                .minContribution(null)
                .contribution(null)
                .build();
    }

    private String maskRrn(String rrn) {
        if (rrn == null || rrn.length() < 7) return rrn;
        return rrn.substring(0, 6) + "-*******";
    }
}
