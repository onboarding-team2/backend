package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team2.onboarding.entity.Employee;
import com.team2.onboarding.entity.DbSchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Builder
public class DbScheduleDetailResponseDto {

    private Long id;
    private String title;

    @JsonProperty("due_date")
    private LocalDate dueDate;

    @JsonProperty("created_date")
    private LocalDate createdDate;

    private String description;
    private String status;

    @JsonProperty("d_day")  // JPA 네이밍컨벤션으로 인해 JSON 중복 key 발생을 막기 위한 변수명 설정
    private String dayCount;

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
    }

    public static DbScheduleDetailResponseDto from(DbSchedule schedule) {
        LocalDate today = LocalDate.now();
        long days = ChronoUnit.DAYS.between(today, schedule.getDueDate());
        String dayCount;
        if ("DONE".equals(schedule.getStatus())) {
            dayCount = "완료";
        } else if (days < 0) {
            dayCount = Math.abs(days) + "일 초과";
        } else if (days == 0) {
            dayCount = "D-Day";
        } else {
            dayCount = days + "일 전";
        }

        List<TargetEmployeeDto> employeeDtos = schedule.getTargetEmployees().stream()
                .map(e -> DbScheduleDetailResponseDto.TargetEmployeeDto.builder()
                        .employeeId(e.getId())
                        .name(e.getName())
                        .companyName(e.getCompany() != null ? e.getCompany().getCompanyName() : null)
                        .build())
                .toList();

        String companyName = schedule.getCompany() != null ? schedule.getCompany().getCompanyName() : null;
        String brn = schedule.getCompany() != null ? schedule.getCompany().getBrn() : null;
        String planType = schedule.getCompany() != null && schedule.getCompany().getPlanType() != null
                ? schedule.getCompany().getPlanType().name() : null;

        return DbScheduleDetailResponseDto.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .dueDate(schedule.getDueDate())
                .createdDate(schedule.getCreatedDate())
                .description(schedule.getDescription())
                .status(schedule.getStatus())
                .dayCount(dayCount)
                .companyName(companyName)
                .brn(brn)
                .planType(planType)
                .targetEmployees(employeeDtos)
                .build();
    }
}
