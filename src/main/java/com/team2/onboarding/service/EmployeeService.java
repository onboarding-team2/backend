package com.team2.onboarding.service;

import com.team2.onboarding.dto.EmployeeDetailResponseDto;
import com.team2.onboarding.dto.EmployeeListItemDto;
import com.team2.onboarding.dto.EmployeeListResponseDto;
import com.team2.onboarding.entity.AnnualSalary;
import com.team2.onboarding.entity.CompanyRetirement;
import com.team2.onboarding.entity.Contribution;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.EmployeeRetirement;
import com.team2.onboarding.enums.EmployeeType;
import com.team2.onboarding.enums.PlanType;
import com.team2.onboarding.repository.AnnualSalaryRepository;
import com.team2.onboarding.repository.CompanyRetirementRepository;
import com.team2.onboarding.repository.ContributionRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.EmployeeRetirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 가입자(Employee) 조회 서비스.
 *
 * 회사 단위로 격리해서 조회 — companyId 인자가 곧 보안 경계다.
 * (지금은 컨트롤러가 쿼리 파라미터로 받지만 추후 JWT 에서 추출하도록 교체할 것.)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeRetirementRepository employeeRetirementRepository;
    private final CompanyRetirementRepository companyRetirementRepository;
    private final AnnualSalaryRepository annualSalaryRepository;
    private final ContributionRepository contributionRepository;

    /**
     * 가입자 명부 조회.
     *
     * @param companyId Company.companyId
     * @param name      이름 검색 키워드 (null/빈문자열 = 전체)
     * @param status    "ACTIVE" 재직 / "RETIRED" 퇴직 / null = 전체
     */
    public EmployeeListResponseDto getEmployees(
            String companyId,
            String name,
            String status,
            int page,
            int size
    ) {
        Boolean onlyActive = parseStatus(status);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "memberId"));

        Page<Employee> employeePage = employeeRepository.searchByCompany(
                companyId,
                name,
                onlyActive,
                pageable
        );

        // 한 페이지에 잡힌 employee 들의 retirement 를 한 번에 모아 매핑
        List<Long> employeeIds = employeePage.getContent().stream().map(Employee::getId).toList();
        Map<Long, EmployeeRetirement> retirementMap = new HashMap<>();
        if (!employeeIds.isEmpty()) {
            employeeRetirementRepository.findByEmployee_IdIn(employeeIds)
                    .forEach(r -> retirementMap.put(r.getEmployee().getId(), r));
        }

        // 회사 단위 값(planType, contributionPaid) 은 1회만 조회
        String planType = companyRetirementRepository.findByCompany_CompanyId(companyId)
                .map(CompanyRetirement::getPlanType)
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

    /** 가입자 상세 조회. 다른 회사 직원은 404. */
    public EmployeeDetailResponseDto getEmployeeDetail(String companyId, Long employeeId) {
        Employee employee = employeeRepository.findByIdAndCompany_CompanyId(employeeId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("가입자를 찾을 수 없습니다."));

        EmployeeRetirement retirement =
                employeeRetirementRepository.findByEmployee_Id(employee.getId()).orElse(null);

        String planType = companyRetirementRepository.findByCompany_CompanyId(companyId)
                .map(CompanyRetirement::getPlanType)
                .map(PlanType::name)
                .orElse(null);

        List<EmployeeDetailResponseDto.AnnualSalaryDto> salaries =
                annualSalaryRepository.findByEmployee_IdOrderByYearDesc(employee.getId()).stream()
                        .map(this::toSalaryDto)
                        .toList();

        return EmployeeDetailResponseDto.builder()
                .id(employee.getId())
                .memberId(employee.getMemberId())
                .name(employee.getName())
                .rrnMasked(maskRrn(employee.getRrn()))
                .company(EmployeeDetailResponseDto.CompanyInfo.builder()
                        .companyId(employee.getCompany().getCompanyId())
                        .companyName(employee.getCompany().getCompanyName())
                        .planType(planType)
                        .build())
                .retirement(toRetirementInfo(retirement))
                .annualSalaries(salaries)
                .build();
    }

    // -----------------------------------------------------------------
    // 매핑/유틸
    // -----------------------------------------------------------------

    private EmployeeListItemDto toListItem(
            Employee e,
            EmployeeRetirement r,
            String planType,
            boolean contributionPaid
    ) {
        return EmployeeListItemDto.builder()
                .id(e.getId())
                .memberId(e.getMemberId())
                .name(e.getName())
                .position(r != null && r.getEmployeeType() != null
                        ? r.getEmployeeType().getDescription()
                        : null)
                .joinDate(r != null ? r.getJoinDate() : null)
                .planType(planType)
                .balance(r != null ? r.getBalance() : null)
                .contributionPaid(contributionPaid)
                .status(toStatusLabel(r))
                .build();
    }

    private EmployeeDetailResponseDto.RetirementInfo toRetirementInfo(EmployeeRetirement r) {
        if (r == null) return null;
        EmployeeType type = r.getEmployeeType();
        return EmployeeDetailResponseDto.RetirementInfo.builder()
                .employeeAccount(r.getEmployeeAccount())
                .employeeType(type != null ? type.name() : null)
                .position(type != null ? type.getDescription() : null)
                .joinDate(r.getJoinDate())
                .startDate(r.getStartDate())
                .effectiveDate(r.getEffectiveDate())
                .terminationDate(r.getTerminationDate())
                .defaultOption(r.getDefaultOption())
                .balance(r.getBalance())
                .status(toStatusLabel(r))
                .build();
    }

    private EmployeeDetailResponseDto.AnnualSalaryDto toSalaryDto(AnnualSalary s) {
        return EmployeeDetailResponseDto.AnnualSalaryDto.builder()
                .year(s.getYear())
                .salary(s.getSalary())
                .build();
    }

    private String toStatusLabel(EmployeeRetirement r) {
        if (r == null) return null;
        return r.getTerminationDate() == null ? "재직" : "퇴직";
    }

    private Boolean parseStatus(String status) {
        if (status == null || status.isBlank()) return null;
        return switch (status.toUpperCase()) {
            case "ACTIVE" -> Boolean.TRUE;
            case "RETIRED" -> Boolean.FALSE;
            default -> throw new IllegalArgumentException("status 는 ACTIVE 또는 RETIRED 여야 합니다.");
        };
    }

    /**
     * 회사가 "이번 달" 부담금을 납부했는지.
     * TODO: 노션 스펙의 "부담금 납입 y/n" 정의가 정해지면 그에 맞게 수정.
     */
    private boolean isContributionPaidThisMonth(String companyId) {
        Optional<Contribution> latest =
                contributionRepository.findTopByCompany_CompanyIdOrderByPaidDateDesc(companyId);
        if (latest.isEmpty()) return false;
        LocalDate paid = latest.get().getPaidDate();
        return paid != null && YearMonth.from(paid).equals(YearMonth.now());
    }

    /** 주민번호 마스킹: 앞 6자리만 남기고 뒷자리 마스킹. */
    private String maskRrn(String rrn) {
        if (rrn == null || rrn.length() < 7) return rrn;
        return rrn.substring(0, 6) + "-*******";
    }
}
