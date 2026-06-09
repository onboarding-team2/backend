package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class PortfolioResponseDto {

    @JsonProperty("portfolio_items")
    private List<PortfolioCategoryDto> portfolioItems;

    @JsonProperty("maturing_products")
    private List<MaturingProductDto> maturingProducts;

    @JsonProperty("average_return_rate")
    private BigDecimal averageReturnRate;

    @JsonProperty("principal_guaranteed_ratio")
    private BigDecimal principalGuaranteedRatio;
}
