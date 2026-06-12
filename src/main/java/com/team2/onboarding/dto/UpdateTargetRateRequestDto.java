package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class UpdateTargetRateRequestDto {

    @JsonProperty("target_return_rate")
    private BigDecimal targetReturnRate;
}
