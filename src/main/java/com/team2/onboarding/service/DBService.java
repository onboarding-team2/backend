package com.team2.onboarding.service;

import com.team2.onboarding.dto.DBDashboardResponseDto;
import com.team2.onboarding.dto.DbMemberItemDto;
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

import java.util.List;
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

    public List<DbMemberItemDto> getMembers(String companyId) {
        Long id = Long.parseLong(companyId);

        List<Employee> employees = employeeRepository.findByCompany_Id(id);

        Map<Long, EmployeeRetirementDb> retirementMap = employeeRetirementDbRepository
                .findByEmployee_Company_Id(id)
                .stream()
                .collect(Collectors.toMap(r -> r.getEmployee().getId(), r -> r));

        return employees.stream()
                .map(e -> {
                    EmployeeRetirementDb erd = retirementMap.get(e.getId());
                    EmployeeType type = e.getEmployeeType();
                    return DbMemberItemDto.builder()
                            .id(e.getId())
                            .name(e.getName())
                            .position(type != null ? type.getDescription() : null)
                            .startDate(e.getStartDate())
                            .joinDate(erd != null ? erd.getJoinDate() : null)
                            .hasIrpAccount(erd != null ? erd.getHasIrpAccount() : null)
                            .balance(null)
                            .status(e.getTerminationDate() != null ? "퇴직" : "재직")
                            .build();
                })
                .toList();
    }

    public Object getDeadlines(String companyId) {
        return null;
    }

    public Object getDocuments(String companyId) {
        return null;
    }
}
