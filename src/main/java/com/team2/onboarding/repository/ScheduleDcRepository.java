package com.team2.onboarding.repository;

import com.team2.onboarding.entity.ScheduleDc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleDcRepository extends JpaRepository<ScheduleDc, Long> {

    List<ScheduleDc> findByCompany_IdOrderByDueDateAsc(Long companyId);
}
