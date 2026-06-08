package com.team2.onboarding.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeListItemDto {

    private Long id;
    private String name;
    private String position;
    private LocalDate joinDate;
    private String planType;
    private Long balance;
    private Boolean contributionPaid;
    private String status;

    @Builder
    private EmployeeListItemDto(
            Long id,
            String name,
            String position,
            LocalDate joinDate,
            String planType,
            Long balance,
            Boolean contributionPaid,
            String status
    ) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.joinDate = joinDate;
        this.planType = planType;
        this.balance = balance;
        this.contributionPaid = contributionPaid;
        this.status = status;
    }
}
