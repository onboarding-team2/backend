package com.team2.onboarding.repository;

import com.team2.onboarding.entity.PortfolioSimulationDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioSimulationDbRepository extends JpaRepository<PortfolioSimulationDb, Long> {

    List<PortfolioSimulationDb> findByCompanyRetirementDb_IdOrderByCreatedAtDesc(Long companyRetirementDbId);
}
