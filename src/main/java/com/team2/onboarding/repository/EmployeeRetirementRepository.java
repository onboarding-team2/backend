package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.EmployeeRetirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeRetirementRepository
        extends JpaRepository<EmployeeRetirement, Long> {

    long countByEmployee_Company_IdAndDefaultOptionFalse(Long companyId);
}
    List<EmployeeRetirement> findByEmployee_CompanyAndTerminationDateAfterOrderByTerminationDate(
            Company company, LocalDate date);
}
