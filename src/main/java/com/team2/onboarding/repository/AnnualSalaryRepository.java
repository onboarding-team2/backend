package com.team2.onboarding.repository;

import com.team2.onboarding.entity.AnnualSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnnualSalaryRepository
        extends JpaRepository<AnnualSalary, Long> {

    List<AnnualSalary> findByEmployee_IdOrderByYearDesc(Long employeeId);

    @Query("""
        SELECT COALESCE(SUM(a.contribution), 0)
        FROM AnnualSalary a
        WHERE a.employee.company.id = :companyId AND a.year = :year
    """)
    Long sumContributionByCompanyAndYear(@Param("companyId") Long companyId, @Param("year") String year);
}
