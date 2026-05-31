package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContributionRepository
        extends JpaRepository<Contribution, Long> {

    Optional<Contribution> findByCompany_Id(Long companyId);
}