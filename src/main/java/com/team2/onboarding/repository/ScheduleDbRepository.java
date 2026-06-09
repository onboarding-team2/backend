package com.team2.onboarding.repository;

import com.team2.onboarding.entity.ScheduleDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleDbRepository extends JpaRepository<ScheduleDb, Long> {

    List<ScheduleDb> findByCompany_IdOrderByDueDateAsc(Long companyId);

    List<ScheduleDb> findByCompany_IdAndDueDateBetweenOrderByDueDateAsc(
            Long companyId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT s FROM ScheduleDb s WHERE s.company.id = :companyId " +
            "AND LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ScheduleDb> searchByKeyword(@Param("companyId") Long companyId, @Param("keyword") String keyword);
}
