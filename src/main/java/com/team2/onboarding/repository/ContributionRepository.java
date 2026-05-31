package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContributionRepository
        extends JpaRepository<Contribution, String> {

    /** 회사의 가장 최근 부담금 납입 1건. */
    Optional<Contribution> findTopByCompany_CompanyIdOrderByPaidDateDesc(String companyId);
}
