package com.team2.onboarding.service;

import com.team2.onboarding.dto.*;
import jakarta.persistence.EntityNotFoundException;
import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.DbSchedule;
import com.team2.onboarding.repository.CompanyRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.DbScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DbScheduleService {

    private final DbScheduleRepository dbScheduleRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    public DbScheduleResponseDto getSchedules(Long companyId, Integer period, String keyword) {
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (period != null) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = period == 1
                    ? startDate.plusMonths(1).minusDays(1)
                    : startDate.plusMonths(2).minusDays(1);
        } else {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = LocalDate.of(startDate.getYear(), 12, 31);
        }

        List<DbSchedule> schedules =
                dbScheduleRepository.searchSchedules(
                        companyId,
                        (keyword == null || keyword.isBlank()) ? null : keyword,
                        startDate,
                        endDate
                );

        return DbScheduleResponseDto.of(schedules);
    }

    public DbScheduleDetailResponseDto getScheduleDetail(Long scheduleId, Long companyId) {
        DbSchedule schedule = dbScheduleRepository.findByIdAndCompany_Id(scheduleId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다."));

        return DbScheduleDetailResponseDto.from(schedule);
    }

    @Transactional
    public DbScheduleDetailResponseDto createSchedule(Long companyId, DbScheduleCreateRequestDto request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다. id=" + companyId));

        List<Employee> targetEmployees = request.getEmployeeIds() != null && !request.getEmployeeIds().isEmpty()
                ? employeeRepository.findByIdInAndCompany_Id(request.getEmployeeIds(), companyId) : new ArrayList<>();

        if (request.getEmployeeIds() != null
                && !request.getEmployeeIds().isEmpty()  // 연관가입자가 없는 경우 기업 전체가 대상이므로 Empty 검증
                && targetEmployees.size() != request.getEmployeeIds().size()) {
            throw new IllegalArgumentException("유효하지 않은 가입자가 포함되어 있습니다.");
        }

        DbSchedule schedule = DbSchedule.builder()
                .title(request.getTitle())
                .dueDate(request.getDueDate())
                .description(request.getDescription())
                .status("ACTIVE")
                .isMandatory(Boolean.FALSE)
                .createdDate(LocalDate.now())
                .targetEmployees(targetEmployees)
                .company(company)
                .build();

        DbSchedule saved = dbScheduleRepository.save(schedule);
        return DbScheduleDetailResponseDto.from(saved);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, Long companyId) {
        DbSchedule schedule = dbScheduleRepository.findByIdAndCompany_Id(scheduleId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다. id=" + scheduleId));

        if (Boolean.TRUE.equals(schedule.getIsMandatory())) {
            throw new IllegalStateException("삭제할 수 없는 일정입니다. id=" + scheduleId);
        }

        dbScheduleRepository.delete(schedule);
    }

    @Transactional
    public DbScheduleDetailResponseDto completeSchedule(Long scheduleId, Long companyId) {
        DbSchedule schedule = dbScheduleRepository.findByIdAndCompany_Id(scheduleId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다. id=" + scheduleId));
        schedule.complete();
        dbScheduleRepository.save(schedule);
        return DbScheduleDetailResponseDto.from(schedule);
    }
}
