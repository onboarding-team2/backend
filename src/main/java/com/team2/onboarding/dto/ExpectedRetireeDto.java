package com.team2.onboarding.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ExpectedRetireeDto {
    private int rank;
    private String name;
    private String memberId;
    private LocalDate retirementDate;
    private String retirementType;
}
