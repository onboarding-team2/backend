package com.team2.onboarding.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.team2.onboarding.dto.ScheduleDbCreateRequestDto;
import com.team2.onboarding.dto.ScheduleDbDetailResponseDto;
import com.team2.onboarding.dto.ScheduleDbResponseDto;
import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.ScheduleDb;
import com.team2.onboarding.repository.CompanyRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.ScheduleDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleDbService {

    private final ScheduleDbRepository scheduleDbRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper;

    public ScheduleDbResponseDto getSchedules(Long companyId, Integer period, String keyword) {
        List<ScheduleDb> schedules;

        if (keyword != null && !keyword.isBlank()) {
            schedules = scheduleDbRepository.searchByKeyword(companyId, keyword);
        } else if (period != null) {
            LocalDate startDate = LocalDate.now().withDayOfMonth(1);
            LocalDate endDate = startDate.plusMonths(period).minusDays(1);
            schedules = scheduleDbRepository.findByCompany_IdAndDueDateBetweenOrderByDueDateAsc(
                    companyId, startDate, endDate);
        } else {
            schedules = scheduleDbRepository.findByCompany_IdOrderByDueDateAsc(companyId);
        }

        return ScheduleDbResponseDto.of(schedules);
    }

    public ScheduleDbDetailResponseDto getScheduleDetail(Long scheduleId) {
        ScheduleDb schedule = scheduleDbRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. id=" + scheduleId));
        List<Employee> employees = parseTargetEmployees(schedule.getTargetEmployees());
        return ScheduleDbDetailResponseDto.from(schedule, employees);
    }

    @Transactional
    public ScheduleDbDetailResponseDto createSchedule(Long companyId, ScheduleDbCreateRequestDto request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다. id=" + companyId));

        ScheduleDb schedule = ScheduleDb.builder()
                .title(request.getTitle())
                .dueDate(request.getDueDate())
                .description(request.getDescription())
                .status("ACTIVE")
                .createdDate(LocalDate.now())
                .targetEmployees(serializeEmployeeIds(request.getEmployeeIds()))
                .company(company)
                .build();

        ScheduleDb saved = scheduleDbRepository.save(schedule);
        List<Employee> employees = parseTargetEmployees(saved.getTargetEmployees());
        return ScheduleDbDetailResponseDto.from(saved, employees);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        ScheduleDb schedule = scheduleDbRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. id=" + scheduleId));
        scheduleDbRepository.delete(schedule);
    }

    @Transactional
    public ScheduleDbDetailResponseDto completeSchedule(Long scheduleId) {
        ScheduleDb schedule = scheduleDbRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. id=" + scheduleId));
        schedule.complete();
        List<Employee> employees = parseTargetEmployees(schedule.getTargetEmployees());
        return ScheduleDbDetailResponseDto.from(schedule, employees);
    }

    private List<Employee> parseTargetEmployees(String targetEmployeesJson) {
        if (targetEmployeesJson == null || targetEmployeesJson.isBlank()) {
            return List.of();
        }
        try {
            List<Long> ids = objectMapper.readValue(targetEmployeesJson, new TypeReference<List<Long>>() {});
            if (ids == null || ids.isEmpty()) {
                return List.of();
            }
            return employeeRepository.findAllById(ids);
        } catch (Exception e) {
            return List.of();
        }
    }

    private String serializeEmployeeIds(List<Long> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(employeeIds);
        } catch (Exception e) {
            return "[]";
        }
    }
}
