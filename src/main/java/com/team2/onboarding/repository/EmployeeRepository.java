package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository
        extends JpaRepository<Employee, String> {

}