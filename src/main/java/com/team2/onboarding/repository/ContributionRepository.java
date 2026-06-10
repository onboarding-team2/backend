package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Contribution;
import com.team2.onboarding.entity.CompanyRetirementDc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    List<Contribution> findByCompanyRetirementDc(CompanyRetirementDc companyRetirementDc);

    List<Contribution> findByCompanyRetirementDcAndDueDateBetween(
            CompanyRetirementDc companyRetirementDc, LocalDate start, LocalDate end);

    Optional<Contribution> findTopByCompanyRetirementDcAndDueDateBetweenOrderByDueDateAsc(
            CompanyRetirementDc companyRetirementDc, LocalDate start, LocalDate end);
    Optional<Contribution> findTopByCompanyRetirementDcAndDueDateGreaterThanEqualOrderByDueDateAsc(
            CompanyRetirementDc companyRetirementDc, LocalDate date );

    @Query("""
    SELECT COALESCE(SUM(c.contributionAmount), 0)
    FROM Contribution c
    WHERE c.companyRetirementDc.company.id = :companyId
      AND c.status = '납입완료'
    """)
    Long sumPaidContributionByCompanyId(@Param("companyId") Long companyId);
}
