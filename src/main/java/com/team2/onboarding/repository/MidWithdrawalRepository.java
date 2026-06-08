package com.team2.onboarding.repository;

import com.team2.onboarding.entity.MidWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MidWithdrawalRepository extends JpaRepository<MidWithdrawal, Long> {

    List<MidWithdrawal> findByEmployeeRetirementDc_Id(Long employeeRetirementDcId);
}
