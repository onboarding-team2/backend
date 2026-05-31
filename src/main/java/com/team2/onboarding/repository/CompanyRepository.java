package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> { // String -> Long 수정
    Optional<Company> findByBrn(String brn);
    Optional<Company> findByCompanyNameAndBrn(String companyId, String brn); // 비밀번호 찾기 검증용
}