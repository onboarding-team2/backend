package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AssetsCurrentResponseDto {

    @JsonProperty("current_return_rate")
    private double currentReturnRate;

    @JsonProperty("target_return_rate")
    private double targetReturnRate;

    @JsonProperty("return_history")
    private List<ReturnHistoryPoint> returnHistory;

    @JsonProperty("portfolio_by_class")
    private List<AssetClassPortfolio> portfolioByClass;

    @JsonProperty("holdings_by_class")
    private List<AssetClassHoldings> holdingsByClass;

    @JsonProperty("total_amount")
    private long totalAmount;

    @JsonProperty("risk_asset_ratio")
    private double riskAssetRatio;

    @Getter
    @Builder
    public static class ReturnHistoryPoint {
        @JsonProperty("base_date")
        private String baseDate;
        @JsonProperty("return_rate")
        private double returnRate;
    }

    @Getter
    @Builder
    public static class AssetClassPortfolio {
        @JsonProperty("class_code")
        private String classCode;
        @JsonProperty("class_name")
        private String className;
        private long amount;
        private double pct;
        private String color;
    }

    @Getter
    @Builder
    public static class AssetClassHoldings {
        @JsonProperty("class_code")
        private String classCode;
        @JsonProperty("class_name")
        private String className;
        private String color;
        private List<ProductHolding> products;
    }

    @Getter
    @Builder
    public static class ProductHolding {
        @JsonProperty("product_name")
        private String productName;
        private String provider;
        @JsonProperty("return_rate")
        private double returnRate;
        private long amount;
    }
}
