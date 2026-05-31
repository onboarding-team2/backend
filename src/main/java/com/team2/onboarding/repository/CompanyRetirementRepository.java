package com.team2.onboarding.repository;

import com.team2.onboarding.entity.CompanyRetirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRetirementRepository
        extends JpaRepository<CompanyRetirement, String> {

    Optional<CompanyRetirement> findByCompany_CompanyId(String companyId);
}
