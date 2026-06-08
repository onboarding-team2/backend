package com.team2.onboarding.repository;

import com.team2.onboarding.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findByEmployeeRetirementDc_Id(Long employeeRetirementDcId);
}
