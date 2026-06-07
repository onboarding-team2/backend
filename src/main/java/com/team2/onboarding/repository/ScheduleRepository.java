package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Schedule;
import com.team2.onboarding.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByCompanyRetirement_Company_IdOrderByDueDateAsc(Long companyId);

    List<Schedule> findByCompanyRetirement_Company_IdAndDueDateBetweenOrderByDueDateAsc(
            Long companyId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT s FROM Schedule s WHERE s.companyRetirement.company.id = :companyId " +
            "AND (LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.companyRetirement.company.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Schedule> searchByKeyword(@Param("companyId") Long companyId, @Param("keyword") String keyword);

    long countByCompanyRetirement_Company_IdAndStatus(Long companyId, ScheduleStatus status);

    List<Schedule> findByCompanyRetirement_Company_IdAndDueDateBetweenAndStatusOrderByDueDateAsc(
            Long companyId, LocalDate startDate, LocalDate endDate, ScheduleStatus status);
}
