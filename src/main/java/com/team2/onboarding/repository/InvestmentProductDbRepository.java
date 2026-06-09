package com.team2.onboarding.repository;

import com.team2.onboarding.entity.InvestmentProductDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestmentProductDbRepository extends JpaRepository<InvestmentProductDb, Long> {

    List<InvestmentProductDb> findByCompanyRetirementDb_Id(Long companyRetirementDbId);
}
