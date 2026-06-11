package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class SimulationSaveRequestDto {

    @JsonProperty("simulation_name")
    private String simulationName;

    @JsonProperty("preset_type")
    private String presetType;

    @JsonProperty("expected_return_rate")
    private BigDecimal expectedReturnRate;

    @JsonProperty("risk_asset_ratio")
    private BigDecimal riskAssetRatio;

    private List<SimulationItemDto> items;

    @Getter
    @NoArgsConstructor
    public static class SimulationItemDto {
        @JsonProperty("asset_class_id")
        private Long assetClassId;
        @JsonProperty("product_master_id")
        private Long productMasterId;
        @JsonProperty("weight_pct")
        private BigDecimal weightPct;
        @JsonProperty("applied_return")
        private BigDecimal appliedReturn;
    }
}
