package com.team2.onboarding.service;

import com.team2.onboarding.dto.DcScheduleCreateRequestDto;
import com.team2.onboarding.dto.DcScheduleDetailResponseDto;
import com.team2.onboarding.dto.DcScheduleResponseDto;
import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.DcSchedule;
import com.team2.onboarding.repository.CompanyRepository;
import com.team2.onboarding.repository.EmployeeRepository;
import com.team2.onboarding.repository.DcScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DcScheduleService {

    private final DcScheduleRepository dcScheduleRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    public DcScheduleResponseDto getSchedules(Long companyId, Integer period, String keyword) {
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (period != null) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = period == 1
                    ? startDate.plusMonths(1).minusDays(1)
                    : startDate.plusMonths(2).minusDays(1);
        }

        List<DcSchedule> schedules =
                dcScheduleRepository.searchSchedules(
                        companyId,
                        (keyword == null || keyword.isBlank()) ? null : keyword,
                        startDate,
                        endDate
                );

        return DcScheduleResponseDto.of(schedules);
    }

    public DcScheduleDetailResponseDto getScheduleDetail(Long scheduleId, Long companyId) {
        DcSchedule schedule = dcScheduleRepository.findByIdAndCompany_Id(scheduleId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다."));

        return DcScheduleDetailResponseDto.from(schedule);
    }

    @Transactional
    public DcScheduleDetailResponseDto createSchedule(Long companyId, DcScheduleCreateRequestDto request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다. id=" + companyId));

        List<Employee> targetEmployees = request.getEmployeeIds() != null && !request.getEmployeeIds().isEmpty()
                ? employeeRepository.findByIdInAndCompany_Id(request.getEmployeeIds(), companyId) : new ArrayList<>();

        if (request.getEmployeeIds() != null
                && !request.getEmployeeIds().isEmpty()  // 연관가입자가 없는 경우 기업 전체가 대상이므로 Empty 검증
                && targetEmployees.size() != request.getEmployeeIds().size()) {
            throw new IllegalArgumentException("유효하지 않은 가입자가 포함되어 있습니다.");
        }

        DcSchedule schedule = DcSchedule.builder()
                .title(request.getTitle())
                .dueDate(request.getDueDate())
                .description(request.getDescription())
                .status("ACTIVE")
                .createdDate(LocalDate.now())
                .targetEmployees(targetEmployees)
                .company(company)
                .build();

        DcSchedule saved = dcScheduleRepository.save(schedule);
        return DcScheduleDetailResponseDto.from(saved);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, Long companyId) {
        DcSchedule schedule = dcScheduleRepository.findByIdAndCompany_Id(scheduleId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다. id=" + scheduleId));
        dcScheduleRepository.delete(schedule);
    }

    @Transactional
    public DcScheduleDetailResponseDto completeSchedule(Long scheduleId, Long companyId) {
        DcSchedule schedule = dcScheduleRepository.findByIdAndCompany_Id(scheduleId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다. id=" + scheduleId));
        schedule.complete();
        return DcScheduleDetailResponseDto.from(schedule);
    }
}
