package com.team2.onboarding.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DcContributionStatusResponseDto {
    private String cycle;
    private List<ContributionChartItemDto> items;
}
