package com.team2.onboarding.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DcContributionStatusResponseDto {
    private int year;
    private Long expectedAmount;
    private List<MonthlyPaymentDto> payments;
}
