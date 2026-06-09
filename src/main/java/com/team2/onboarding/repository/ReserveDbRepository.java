package com.team2.onboarding.repository;

import com.team2.onboarding.entity.ReserveDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReserveDbRepository extends JpaRepository<ReserveDb, Long> {

    Optional<ReserveDb> findTopByCompanyRetirementDb_IdOrderByBaseDateDesc(Long companyRetirementDbId);
}
