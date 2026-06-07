package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

public interface ContributionRepository
        extends JpaRepository<Contribution, Long> {

    Optional<Contribution> findByCompany_Id(Long companyId);

    /** 회사의 가장 최근 부담금 납입 1건. */
    Optional<Contribution> findTopByCompany_CompanyIdOrderByPaidDateDesc(String companyId);
    List<Contribution> findByCompanyAndPaidDateBetween(Company company, LocalDate start, LocalDate end);

    @Query("""
        SELECT COALESCE(SUM(c.contributionAmount), 0)
        FROM Contribution c
        WHERE c.company.companyId = :companyId
    """)
    Long sumContributionAmountByCompanyId(@Param("companyId") String companyId);

}
