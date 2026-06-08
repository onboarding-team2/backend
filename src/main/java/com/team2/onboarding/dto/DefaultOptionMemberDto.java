package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DefaultOptionMemberDto {

    private String name;

    @JsonProperty("join_date")
    private LocalDate joinDate;

    @JsonProperty("days_elapsed")
    private long daysElapsed;
}
