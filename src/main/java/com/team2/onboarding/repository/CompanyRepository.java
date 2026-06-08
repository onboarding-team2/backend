package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByBrn(String brn);

    Optional<Company> findByCompanyNameAndBrn(String companyName, String brn);
}
