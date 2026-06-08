package com.team2.onboarding.repository;

import com.team2.onboarding.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {

    List<FeePayment> findByCompanyRetirementDc_Id(Long companyRetirementDcId);
}
