package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class ScheduleDcCreateRequestDto {

    private String title;

    @JsonProperty("due_date")
    private LocalDate dueDate;

    private String description;

    @JsonProperty("employee_ids")
    private List<Long> employeeIds;
}
