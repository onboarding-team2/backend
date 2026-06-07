package com.team2.onboarding.service;

import com.team2.onboarding.dto.ScheduleCreateRequestDto;
import com.team2.onboarding.dto.ScheduleDetailResponseDto;
import com.team2.onboarding.dto.ScheduleResponseDto;
import com.team2.onboarding.entity.CompanyRetirement;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.Schedule;
import com.team2.onboarding.enums.ScheduleStatus;
import com.team2.onboarding.repository.CompanyRetirementRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CompanyRetirementRepository companyRetirementRepository;
    private final EmployeeRepository employeeRepository;

    public ScheduleResponseDto getSchedules(Long companyId, Integer period, String keyword) {
        List<Schedule> schedules;

        if (keyword != null && !keyword.isBlank()) {
            schedules = scheduleRepository.searchByKeyword(companyId, keyword);
        } else if (period != null) {
            LocalDate startDate = LocalDate.now().withDayOfMonth(1);
            LocalDate endDate;
            if (period == 1) {
                endDate = startDate.plusMonths(1).minusDays(1);
            } else {
                endDate = startDate.plusMonths(2).minusDays(1);
            }
            schedules = scheduleRepository
                    .findByCompanyRetirement_Company_IdAndDueDateBetweenOrderByDueDateAsc(
                            companyId, startDate, endDate);
        } else {
            schedules = scheduleRepository
                    .findByCompanyRetirement_Company_IdOrderByDueDateAsc(companyId);
        }

        return ScheduleResponseDto.of(schedules);
    }

    public ScheduleDetailResponseDto getScheduleDetail(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. id=" + scheduleId));
        return ScheduleDetailResponseDto.from(schedule);
    }

    @Transactional
    public ScheduleDetailResponseDto createSchedule(Long companyId, ScheduleCreateRequestDto request) {
        CompanyRetirement companyRetirement = null;
        if (request.getCompanyRetirementId() != null) {
            companyRetirement = companyRetirementRepository.findById(
                            String.valueOf(request.getCompanyRetirementId()))
                    .orElseThrow(() -> new IllegalArgumentException(
                            "기업퇴직연금정보를 찾을 수 없습니다. id=" + request.getCompanyRetirementId()));
        } else {
            List<CompanyRetirement> allRetirements = companyRetirementRepository.findAll();
            companyRetirement = allRetirements.stream()
                    .filter(cr -> cr.getCompany() != null && cr.getCompany().getId().equals(companyId))
                    .findFirst()
                    .orElse(null);
        }

        List<Employee> targetEmployees = new ArrayList<>();
        if (request.getEmployeeIds() != null && !request.getEmployeeIds().isEmpty()) {
            targetEmployees = employeeRepository.findAllById(request.getEmployeeIds());
        }

        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .dueDate(request.getDueDate())
                .description(request.getDescription())
                .status(ScheduleStatus.ACTIVE)
                .createdDate(LocalDate.now())
                .companyRetirement(companyRetirement)
                .targetEmployees(targetEmployees)
                .build();

        Schedule saved = scheduleRepository.save(schedule);
        return ScheduleDetailResponseDto.from(saved);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. id=" + scheduleId));
        scheduleRepository.delete(schedule);
    }

    @Transactional
    public ScheduleDetailResponseDto completeSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. id=" + scheduleId));
        schedule.complete();
        return ScheduleDetailResponseDto.from(schedule);
    }
}
