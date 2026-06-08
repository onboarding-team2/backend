package com.team2.onboarding.repository;

import com.team2.onboarding.entity.EmployeeRetirementDc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeRetirementDcRepository extends JpaRepository<EmployeeRetirementDc, Long> {

    Optional<EmployeeRetirementDc> findByEmployeeId(Long employeeId);

    List<EmployeeRetirementDc> findByEmployee_Company_Id(Long companyId);

    List<EmployeeRetirementDc> findByEmployee_Company_IdAndTerminationDateAfterOrderByTerminationDate(
            Long companyId, LocalDate date);

    long countByEmployee_Company_IdAndDefaultOption(Long companyId, String defaultOption);
}
