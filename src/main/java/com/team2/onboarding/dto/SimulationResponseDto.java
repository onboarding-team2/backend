package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SimulationResponseDto {

    private Long id;

    @JsonProperty("simulation_name")
    private String simulationName;

    @JsonProperty("expected_return_rate")
    private double expectedReturnRate;

    @JsonProperty("risk_asset_ratio")
    private double riskAssetRatio;

    @JsonProperty("preset_type")
    private String presetType;

    private String status;

    @JsonProperty("created_at")
    private String createdAt;

    private List<SimulationItemResponse> items;

    @Getter
    @Builder
    public static class SimulationItemResponse {
        @JsonProperty("asset_class_id")
        private Long assetClassId;
        @JsonProperty("class_code")
        private String classCode;
        @JsonProperty("class_name")
        private String className;
        @JsonProperty("product_master_id")
        private Long productMasterId;
        @JsonProperty("product_name")
        private String productName;
        @JsonProperty("weight_pct")
        private double weightPct;
        @JsonProperty("applied_return")
        private double appliedReturn;
    }
}
