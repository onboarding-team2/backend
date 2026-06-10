package com.team2.onboarding.service;

import com.team2.onboarding.dto.DBDashboardResponseDto;
import com.team2.onboarding.dto.DbMemberItemDto;
import com.team2.onboarding.dto.PageResponse;
import com.team2.onboarding.entity.CompanyRetirementDb;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.EmployeeRetirementDb;
import com.team2.onboarding.entity.ReserveDb;
import com.team2.onboarding.enums.EmployeeType;
import com.team2.onboarding.repository.CompanyRetirementDbRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.EmployeeRetirementDbRepository;
import com.team2.onboarding.repository.InvestmentProductDbRepository;
import com.team2.onboarding.repository.ReserveDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DBService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeRetirementDbRepository employeeRetirementDbRepository;
    private final CompanyRetirementDbRepository companyRetirementDbRepository;
    private final ReserveDbRepository reserveDbRepository;
    private final InvestmentProductDbRepository investmentProductDbRepository;

    public DBDashboardResponseDto getDashboard(String companyId) {
        Long id = Long.parseLong(companyId);

        CompanyRetirementDb companyRetirementDb = companyRetirementDbRepository
                .findByCompany_Id(id)
                .orElseThrow(() -> new IllegalArgumentException("DB 퇴직연금 계약 정보를 찾을 수 없습니다."));
        Long dbId = companyRetirementDb.getId();

        ReserveDb reserve = reserveDbRepository
                .findTopByCompanyRetirementDb_IdOrderByBaseDateDesc(dbId)
                .orElseThrow(() -> new IllegalArgumentException("재정검증 데이터를 찾을 수 없습니다."));

        long fundedAmount = investmentProductDbRepository
                .findByCompanyRetirementDb_Id(dbId)
                .stream()
                .mapToLong(p -> {
                    if ("만기완료".equals(p.getStatus())) {
                        return p.getConfirmedAmount();
                    }
                    return Math.round(
                            p.getPrincipal() * (1 + p.getAnnualReturnRate().doubleValue() / 100.0)
                    );
                })
                .sum();

        long memberCount = employeeRetirementDbRepository.countByCompanyRetirementDb_Id(dbId);

        return DBDashboardResponseDto.builder()
                .memberCount(memberCount)
                .fundedAmount(fundedAmount)
                .benefitObligation(reserve.getBenefitObligation())
                .minReserve(reserve.getMinReserve())
                .fundingRatio(reserve.getFundingRatio())
                .shortfallAmount(reserve.getShortfallAmount())
                .additionalDueDate(reserve.getAdditionalDueDate())
                .status(reserve.getStatus())
                .baseDate(reserve.getBaseDate())
                .build();
    }

    /**
     * 가입자 목록 조회(서버 필터링 + 페이지네이션).
     * DB형은 디폴트옵션/부담금 개념이 없어 status/type/irp 필터만 지원.
     */
    public PageResponse<DbMemberItemDto> getMembers(
            String companyId,
            String name,
            List<String> status,
            List<String> type,
            List<String> irp,
            int page,
            int size
    ) {
        Long id = Long.parseLong(companyId);

        List<Employee> employees = employeeRepository.findByCompany_Id(id);

        Map<Long, EmployeeRetirementDb> retirementMap = employeeRetirementDbRepository
                .findByEmployee_Company_Id(id)
                .stream()
                .collect(Collectors.toMap(r -> r.getEmployee().getId(), r -> r));

        Collator collator = Collator.getInstance(Locale.KOREAN);

        List<DbMemberItemDto> all = employees.stream()
                .map(e -> {
                    EmployeeRetirementDb erd = retirementMap.get(e.getId());
                    EmployeeType empType = e.getEmployeeType();
                    return DbMemberItemDto.builder()
                            .id(e.getId())
                            .name(e.getName())
                            .rrnMasked(maskRrn(e.getRrn()))
                            .position(empType != null ? empType.getDescription() : null)
                            .startDate(e.getStartDate())
                            .joinDate(erd != null ? erd.getJoinDate() : null)
                            .hasIrpAccount(erd != null ? erd.getHasIrpAccount() : null)
                            .balance(null)
                            .status(e.getTerminationDate() != null ? "퇴직" : "재직")
                            .build();
                })
                .filter(dto -> matches(dto, name, status, type, irp))
                .sorted((a, b) -> collator.compare(a.getName(), b.getName()))
                .toList();

        return PageResponse.of(all, page, size);
    }

    private boolean matches(DbMemberItemDto dto, String name, List<String> status, List<String> type, List<String> irp) {
        if (name != null && !name.isBlank() && (dto.getName() == null || !dto.getName().contains(name))) return false;
        if (notIn(status, dto.getStatus())) return false;
        if (notIn(type, dto.getPosition())) return false;
        if (notIn(irp, "Y".equals(dto.getHasIrpAccount()) ? "보유" : "미보유")) return false;
        return true;
    }

    private boolean notIn(List<String> filter, String value) {
        return filter != null && !filter.isEmpty() && !filter.contains(value);
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
