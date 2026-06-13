package com.team2.onboarding.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DcMemberItemDto {

    private Long id;
    private String name;
    private String rrnMasked;
    private String position;
    private LocalDate startDate;
    private LocalDate joinDate;
    private String hasIrpAccount;
    private String defaultOption;
    private Long balance;
    private Long minContribution;
    private Long contribution;
    private Boolean contributionPaid;
    private String status;
}
