package com.team2.onboarding.service;

import com.team2.onboarding.dto.DbMemberItemDto;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.EmployeeRetirementDb;
import com.team2.onboarding.enums.EmployeeType;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.EmployeeRetirementDbRepository;
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

    public Object getDashboard(String companyId) {
        return null;
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
