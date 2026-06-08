package com.team2.onboarding.repository;

import com.team2.onboarding.entity.CompanyRetirementDc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRetirementDcRepository extends JpaRepository<CompanyRetirementDc, Long> {

    Optional<CompanyRetirementDc> findByCompanyId(Long companyId);
}
