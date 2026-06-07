package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.EmployeeRetirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeRetirementRepository
        extends JpaRepository<EmployeeRetirement, Long> {

    long countByEmployee_Company_IdAndDefaultOptionFalse(Long companyId);

    Optional<EmployeeRetirement> findByEmployee_Id(Long employeeId);

    List<EmployeeRetirement> findByEmployee_IdIn(List<Long> employeeIds);
    List<EmployeeRetirement> findByEmployee_CompanyAndTerminationDateAfterOrderByTerminationDate(
            Company company, LocalDate date);


    long countByEmployee_Company_CompanyIdAndDefaultOption(
            String companyId,
            Boolean defaultOption
    );
}
