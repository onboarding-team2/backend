package com.team2.onboarding.repository;

import com.team2.onboarding.entity.DbSchedule;
import com.team2.onboarding.entity.DcSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DbScheduleRepository extends JpaRepository<DbSchedule, Long> {
    // getSchedules - companyId(Required), keyword 검색 & period 옵션
    // period: null(당해년도) / 0 (전체년도) / 1 (당월) / 2(당해년도 당월~익월)
    // if (keyword != null && period != null) { 키워드와 기간 모두 포함하여 검색 }
    // if (keyword != null && period == null) { 키워드로만 검색 }
    // if (keyword == null && period != null) { 기간으로만 검색 }
    @Query("SELECT s FROM DbSchedule s WHERE s.company.id = :companyId " +
            "AND (:keyword IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:startDate IS NULL OR s.dueDate >= :startDate) " +
            "AND (:endDate IS NULL OR s.dueDate <= :endDate) " +
            "ORDER BY s.dueDate ASC")
    List<DbSchedule> searchSchedules(
            @Param("companyId") Long companyId,
            @Param("keyword") String keyword,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // getScheduleDetail / deleteSchedule / completeSchedule - 권한 검증 포함 단건 조회
    Optional<DbSchedule> findByIdAndCompany_Id(Long id, Long companyId);
}
