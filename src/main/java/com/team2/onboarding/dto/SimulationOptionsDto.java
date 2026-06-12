package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SimulationOptionsDto {

    private List<AssetClassOption> classes;

    @Getter
    @Builder
    public static class AssetClassOption {
        private Long id;
        @JsonProperty("class_code")
        private String classCode;
        @JsonProperty("class_name")
        private String className;
        @JsonProperty("is_risk")
        private boolean isRisk;
        @JsonProperty("allow_multi")
        private boolean allowMulti;
        @JsonProperty("avg_return_3y")
        private double avgReturn3y;
        private String color;
        private List<ProductOption> products;
    }

    @Getter
    @Builder
    public static class ProductOption {
        private Long id;
        private String name;
        @JsonProperty("return_rate")
        private double returnRate;
        private String tag;
        private String provider;
    }
}
