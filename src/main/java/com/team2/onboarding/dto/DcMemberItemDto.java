package com.team2.onboarding.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DcMemberItemDto {

    private Long id;
    private String name;
    private String position;
    private LocalDate startDate;
    private Long balance;
    private Boolean contributionPaid;
    private String status;
}
