package com.team2.onboarding.repository;

import com.team2.onboarding.entity.ScheduleDc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleDcRepository extends JpaRepository<ScheduleDc, Long> {

    List<ScheduleDc> findByCompany_IdOrderByDueDateAsc(Long companyId);

    List<ScheduleDc> findByCompany_IdAndDueDateBetweenOrderByDueDateAsc(
            Long companyId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT s FROM ScheduleDc s WHERE s.company.id = :companyId " +
            "AND (LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.company.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ScheduleDc> searchByKeyword(@Param("companyId") Long companyId, @Param("keyword") String keyword);
}
