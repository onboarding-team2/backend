package com.team2.onboarding.repository;

import com.team2.onboarding.entity.AnnualSalaryDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnualSalaryDbRepository extends JpaRepository<AnnualSalaryDb, Long> {

    List<AnnualSalaryDb> findByEmployee_IdOrderByYearDesc(Long employeeId);
}
