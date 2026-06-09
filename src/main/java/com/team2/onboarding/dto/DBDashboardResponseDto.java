package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class DBDashboardResponseDto {

    @JsonProperty("member_count")
    private long memberCount;

    @JsonProperty("funded_amount")
    private long fundedAmount;

    @JsonProperty("benefit_obligation")
    private long benefitObligation;

    @JsonProperty("min_reserve")
    private long minReserve;

    @JsonProperty("funding_ratio")
    private BigDecimal fundingRatio;

    @JsonProperty("shortfall_amount")
    private long shortfallAmount;

    @JsonProperty("additional_due_date")
    private LocalDate additionalDueDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("base_date")
    private LocalDate baseDate;
}
