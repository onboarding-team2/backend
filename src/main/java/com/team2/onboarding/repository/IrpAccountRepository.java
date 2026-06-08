package com.team2.onboarding.repository;

import com.team2.onboarding.entity.IrpAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IrpAccountRepository extends JpaRepository<IrpAccount, Long> {

    Optional<IrpAccount> findByEmployeeRetirementDc_Id(Long employeeRetirementDcId);

    List<IrpAccount> findByEmployeeRetirementDc_Employee_Company_Id(Long companyId);
}
