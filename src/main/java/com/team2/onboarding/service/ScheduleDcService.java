package com.team2.onboarding.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.team2.onboarding.dto.ScheduleDcCreateRequestDto;
import com.team2.onboarding.dto.ScheduleDcDetailResponseDto;
import com.team2.onboarding.dto.ScheduleDcResponseDto;
import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.ScheduleDc;
import com.team2.onboarding.repository.CompanyRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.ScheduleDcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleDcService {

    private final ScheduleDcRepository scheduleDcRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper;

    public ScheduleDcResponseDto getSchedules(Long companyId, Integer period, String keyword) {
        List<ScheduleDc> schedules;

        if (keyword != null && !keyword.isBlank()) {
            schedules = scheduleDcRepository.searchByKeyword(companyId, keyword);
        } else if (period != null) {
            LocalDate startDate = LocalDate.now().withDayOfMonth(1);
            LocalDate endDate = startDate.plusMonths(period).minusDays(1);
            schedules = scheduleDcRepository.findByCompany_IdAndDueDateBetweenOrderByDueDateAsc(
                    companyId, startDate, endDate);
        } else {
            schedules = scheduleDcRepository.findByCompany_IdOrderByDueDateAsc(companyId);
        }

        return ScheduleDcResponseDto.of(schedules);
    }

    public ScheduleDcDetailResponseDto getScheduleDetail(Long scheduleId, Long companyId) {
        ScheduleDc schedule = scheduleDcRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. id=" + scheduleId));
        if (!schedule.getCompany().getId().equals(companyId)) {
            throw new IllegalArgumentException("해당 일정에 대한 접근 권한이 없습니다.");
        }
        List<Employee> employees = parseTargetEmployees(schedule.getTargetEmployees(), companyId);
        return ScheduleDcDetailResponseDto.from(schedule, employees);
    }

    @Transactional
    public ScheduleDcDetailResponseDto createSchedule(Long companyId, ScheduleDcCreateRequestDto request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다. id=" + companyId));

        List<Long> employeeIds = request.getEmployeeIds();
        if (employeeIds != null && !employeeIds.isEmpty()) {
            List<Employee> found = employeeRepository.findByIdInAndCompany_Id(employeeIds, companyId);
            if (found.size() != employeeIds.size()) {
                throw new IllegalArgumentException("일부 가입자가 해당 기업에 속하지 않습니다.");
            }
        }

        ScheduleDc schedule = ScheduleDc.builder()
                .title(request.getTitle())
                .dueDate(request.getDueDate())
                .description(request.getDescription())
                .status("예정")
                .createdDate(LocalDate.now())
                .targetEmployees(serializeEmployeeIds(employeeIds))
                .company(company)
                .build();

        ScheduleDc saved = scheduleDcRepository.save(schedule);
        List<Employee> employees = parseTargetEmployees(saved.getTargetEmployees(), companyId);
        return ScheduleDcDetailResponseDto.from(saved, employees);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, Long companyId) {
        ScheduleDc schedule = scheduleDcRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. id=" + scheduleId));
        if (!schedule.getCompany().getId().equals(companyId)) {
            throw new IllegalArgumentException("해당 일정에 대한 접근 권한이 없습니다.");
        }
        scheduleDcRepository.delete(schedule);
    }

    @Transactional
    public ScheduleDcDetailResponseDto completeSchedule(Long scheduleId, Long companyId) {
        ScheduleDc schedule = scheduleDcRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다. id=" + scheduleId));
        if (!schedule.getCompany().getId().equals(companyId)) {
            throw new IllegalArgumentException("해당 일정에 대한 접근 권한이 없습니다.");
        }
        schedule.complete();
        List<Employee> employees = parseTargetEmployees(schedule.getTargetEmployees(), companyId);
        return ScheduleDcDetailResponseDto.from(schedule, employees);
    }

    private List<Employee> parseTargetEmployees(String targetEmployeesJson, Long companyId) {
        if (targetEmployeesJson == null || targetEmployeesJson.isBlank()) {
            return List.of();
        }
        try {
            List<Long> ids = objectMapper.readValue(targetEmployeesJson, new TypeReference<List<Long>>() {});
            if (ids == null || ids.isEmpty()) {
                return List.of();
            }
            return employeeRepository.findByIdInAndCompany_Id(ids, companyId);
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
