package com.team2.onboarding.repository;

import com.team2.onboarding.entity.EmployeeRetirementDc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeRetirementDcRepository extends JpaRepository<EmployeeRetirementDc, Long> {

    Optional<EmployeeRetirementDc> findByEmployeeId(Long employeeId);

    List<EmployeeRetirementDc> findByEmployee_Company_Id(Long companyId);

    List<EmployeeRetirementDc> findByEmployee_Company_IdAndEmployee_TerminationDateAfterOrderByEmployee_TerminationDateAsc(
            Long companyId, LocalDate date);

    long countByEmployee_Company_IdAndDefaultOption(Long companyId, String defaultOption);

    List<EmployeeRetirementDc> findByEmployee_Company_IdAndDefaultOptionOrderByJoinDateAsc(
            Long companyId, String defaultOption);

    @Query("SELECT COALESCE(SUM(e.balance), 0) FROM EmployeeRetirementDc e WHERE e.employee.company.id = :companyId")
    long sumBalanceByCompanyId(@Param("companyId") Long companyId);
}
