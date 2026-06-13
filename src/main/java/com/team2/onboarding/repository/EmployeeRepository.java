package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByCompany_Id(Long companyId);

    List<Employee> findByIdInAndCompany_Id(Collection<Long> ids, Long companyId);

    Optional<Employee> findByIdAndCompanyId(Long id, Long companyId);

    long countByCompany_Id(Long companyId);
}
