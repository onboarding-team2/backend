package com.team2.onboarding.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContributionChartItemDto {
    private String label;
    private Long amount;
    private boolean paid;
}
