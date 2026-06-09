package com.team2.onboarding.repository;

import com.team2.onboarding.entity.CompanyRetirementDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRetirementDbRepository extends JpaRepository<CompanyRetirementDb, Long> {

    Optional<CompanyRetirementDb> findByCompany_Id(Long companyId);
}
