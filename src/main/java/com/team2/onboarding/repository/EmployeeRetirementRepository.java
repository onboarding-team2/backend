package com.team2.onboarding.repository;

import com.team2.onboarding.entity.EmployeeRetirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRetirementRepository
        extends JpaRepository<EmployeeRetirement, String> {

    Optional<EmployeeRetirement> findByEmployee_Id(Long employeeId);

    List<EmployeeRetirement> findByEmployee_IdIn(List<Long> employeeIds);
}
