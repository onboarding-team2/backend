package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository
        extends JpaRepository<Company, String> {

    Optional<Company> findByBrn(String brn);
}