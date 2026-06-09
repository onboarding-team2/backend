package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MaturingProductDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("maturity_date")
    private LocalDate maturityDate;

    @JsonProperty("principal")
    private long principal;

    @JsonProperty("evaluated_amount")
    private long evaluatedAmount;
}
