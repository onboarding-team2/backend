package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Builder
public class ScheduleDetailResponseDto {

    private Long id;
    private String title;

    @JsonProperty("due_date")
    private LocalDate dueDate;

    @JsonProperty("created_date")
    private LocalDate createdDate;

    private String description;
    private String status;

    @JsonProperty("d_day")
    private String dDay;

    @JsonProperty("company_name")
    private String companyName;

    private String brn;

    @JsonProperty("plan_type")
    private String planType;

    @JsonProperty("target_employees")
    private List<TargetEmployeeDto> targetEmployees;

    @Getter
    @Builder
    public static class TargetEmployeeDto {

        @JsonProperty("employee_id")
        private Long employeeId;

        private String name;

        @JsonProperty("company_name")
        private String companyName;

        @JsonProperty("member_id")
        private String memberId;
    }

    public static ScheduleDetailResponseDto from(Schedule schedule) {
        LocalDate today = LocalDate.now();
        long days = ChronoUnit.DAYS.between(today, schedule.getDueDate());
        String dDay;
        if (days < 0) {
            dDay = Math.abs(days) + "일 초과";
        } else if (days == 0) {
            dDay = "D-Day";
        } else {
            dDay = days + "일 전";
        }

        List<TargetEmployeeDto> employees = schedule.getTargetEmployees().stream()
                .map(e -> TargetEmployeeDto.builder()
                        .employeeId(e.getId())
                        .name(e.getName())
                        .companyName(e.getCompany() != null ? e.getCompany().getCompanyName() : null)
                        .memberId(e.getMemberId())
                        .build())
                .toList();

        String companyName = null;
        String brn = null;
        String planType = null;
        if (schedule.getCompanyRetirement() != null) {
            if (schedule.getCompanyRetirement().getCompany() != null) {
                companyName = schedule.getCompanyRetirement().getCompany().getCompanyName();
                brn = schedule.getCompanyRetirement().getCompany().getBrn();
            }
            planType = schedule.getCompanyRetirement().getPlanType() != null
                    ? schedule.getCompanyRetirement().getPlanType().name() : null;
        }

        return ScheduleDetailResponseDto.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .dueDate(schedule.getDueDate())
                .createdDate(schedule.getCreatedDate())
                .description(schedule.getDescription())
                .status(schedule.getStatus().name())
                .dDay(dDay)
                .companyName(companyName)
                .brn(brn)
                .planType(planType)
                .targetEmployees(employees)
                .build();
    }
}
