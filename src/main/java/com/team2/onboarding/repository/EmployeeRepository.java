package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
            SELECT e
              FROM Employee e
              LEFT JOIN FETCH e.company c
              LEFT JOIN com.team2.onboarding.entity.EmployeeRetirementDc r
                    ON r.employee = e
             WHERE c.id = :companyId
               AND (:nameKeyword IS NULL OR :nameKeyword = '' OR e.name LIKE CONCAT('%', :nameKeyword, '%'))
               AND (:onlyActive IS NULL
                    OR (:onlyActive = TRUE  AND r.terminationDate IS NULL)
                    OR (:onlyActive = FALSE AND r.terminationDate IS NOT NULL))
            """)
    Page<Employee> searchByCompany(
            @Param("companyId") Long companyId,
            @Param("nameKeyword") String nameKeyword,
            @Param("onlyActive") Boolean onlyActive,
            Pageable pageable
    );

    Optional<Employee> findByIdAndCompanyId(Long id, Long companyId);

    long countByCompany(Company company);

    long countByCompany_Id(Long companyId);
}
