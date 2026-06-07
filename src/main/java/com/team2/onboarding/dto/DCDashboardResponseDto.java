package com.team2.onboarding.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DCDashboardResponseDto {
    private long totalEmployee;
    private long defaultOptionNotSelected;
    private long totalContributionAmount;
}
