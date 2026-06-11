package com.team2.onboarding.repository;

import com.team2.onboarding.entity.ReturnHistoryDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnHistoryDbRepository extends JpaRepository<ReturnHistoryDb, Long> {
    List<ReturnHistoryDb> findByCompanyRetirementDb_IdOrderByBaseDateAsc(Long companyRetirementDbId);
}
