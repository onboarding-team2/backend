package com.team2.onboarding.repository;

import com.team2.onboarding.entity.InvestmentProductDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvestmentProductDbRepository extends JpaRepository<InvestmentProductDb, Long> {

    List<InvestmentProductDb> findByCompanyRetirementDb_Id(Long companyRetirementDbId);

    @Query("SELECT p FROM InvestmentProductDb p JOIN FETCH p.productMaster WHERE p.companyRetirementDb.id = :id")
    List<InvestmentProductDb> findWithProductMasterByCompanyRetirementDb_Id(@Param("id") Long id);
}
