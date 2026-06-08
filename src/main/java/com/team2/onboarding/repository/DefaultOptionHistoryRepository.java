package com.team2.onboarding.repository;

import com.team2.onboarding.entity.DefaultOptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DefaultOptionHistoryRepository extends JpaRepository<DefaultOptionHistory, Long> {

    List<DefaultOptionHistory> findByEmployeeRetirementDc_Id(Long employeeRetirementDcId);
}
