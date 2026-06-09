package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PortfolioCategoryDto {

    @JsonProperty("category")
    private String category;

    @JsonProperty("amount")
    private long amount;

    @JsonProperty("percent")
    private long percent;
}
