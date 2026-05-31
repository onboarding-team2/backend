package com.team2.onboarding.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlyPaymentDto {
    private int month;
    private Long amount;
    private boolean paid;
}
