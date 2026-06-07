package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

public interface ContributionRepository
        extends JpaRepository<Contribution, Long> {

    Optional<Contribution> findByCompany_Id(Long companyId);
}
    List<Contribution> findByCompanyAndPaidDateBetween(Company company, LocalDate start, LocalDate end);
}
