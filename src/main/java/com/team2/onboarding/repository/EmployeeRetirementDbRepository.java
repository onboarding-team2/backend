package com.team2.onboarding.repository;

import com.team2.onboarding.entity.EmployeeRetirementDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRetirementDbRepository extends JpaRepository<EmployeeRetirementDb, Long> {

    Optional<EmployeeRetirementDb> findByEmployee_Id(Long employeeId);

    List<EmployeeRetirementDb> findByEmployee_Company_Id(Long companyId);

    long countByEmployee_Company_IdAndHasIrpAccount(Long companyId, String hasIrpAccount);
}
