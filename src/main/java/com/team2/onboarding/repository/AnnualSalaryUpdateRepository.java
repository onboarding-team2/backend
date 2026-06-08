package com.team2.onboarding.repository;

import com.team2.onboarding.entity.AnnualSalaryUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnualSalaryUpdateRepository extends JpaRepository<AnnualSalaryUpdate, Long> {

    List<AnnualSalaryUpdate> findByAnnualSalary_EmployeeId(Long employeeId);
}
