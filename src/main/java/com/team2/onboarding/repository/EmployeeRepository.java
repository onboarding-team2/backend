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

    /**
     * 회사 소속 가입자 목록 + 이름 검색 + 퇴직 여부 필터.
     * EmployeeRetirement 를 함께 fetch 해 N+1 을 막는다.
     *
     * @param companyId  Company.companyId (문자열 사번)
     * @param nameKeyword null 또는 빈 문자열이면 전체
     * @param onlyActive null = 전체, true = 재직(termination_date IS NULL), false = 퇴직
     */
    @Query("""
            SELECT e
              FROM Employee e
              LEFT JOIN FETCH e.company c
              LEFT JOIN com.team2.onboarding.entity.EmployeeRetirement r
                    ON r.employee = e
             WHERE c.companyId = :companyId
               AND (:nameKeyword IS NULL OR :nameKeyword = '' OR e.name LIKE CONCAT('%', :nameKeyword, '%'))
               AND (:onlyActive IS NULL
                    OR (:onlyActive = TRUE  AND r.terminationDate IS NULL)
                    OR (:onlyActive = FALSE AND r.terminationDate IS NOT NULL))
            """)
    Page<Employee> searchByCompany(
            @Param("companyId") String companyId,
            @Param("nameKeyword") String nameKeyword,
            @Param("onlyActive") Boolean onlyActive,
            Pageable pageable
    );

    /**
     * 가입자 상세 조회 — 같은 회사 소속인지도 같이 확인.
     */
    Optional<Employee> findByIdAndCompany_CompanyId(Long id, String companyId);
}
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    long countByCompany(Company company);
    long countByCompany_Id(Long companyId);
}
