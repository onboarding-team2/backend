package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Contribution;
import com.team2.onboarding.entity.CompanyRetirementDc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    List<Contribution> findByCompanyRetirementDc(CompanyRetirementDc companyRetirementDc);

    List<Contribution> findByCompanyRetirementDcAndDueDateBetween(
            CompanyRetirementDc companyRetirementDc, LocalDate start, LocalDate end);

    Optional<Contribution> findTopByCompanyRetirementDcAndDueDateBetweenOrderByDueDateAsc(
            CompanyRetirementDc companyRetirementDc, LocalDate start, LocalDate end);
}
