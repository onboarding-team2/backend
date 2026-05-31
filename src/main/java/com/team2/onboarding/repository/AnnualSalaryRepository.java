package com.team2.onboarding.repository;

import com.team2.onboarding.entity.AnnualSalary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnualSalaryRepository
        extends JpaRepository<AnnualSalary, String> {

}