package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "asset_class_master")
public class AssetClassMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_code", nullable = false, unique = true, length = 20)
    private String classCode;

    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    @Column(name = "is_risk_asset", nullable = false)
    private Boolean isRiskAsset;

    @Column(name = "allow_multi", nullable = false)
    private Boolean allowMulti;

    @Column(name = "avg_return_3y", precision = 5, scale = 2)
    private BigDecimal avgReturn3y;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Builder
    private AssetClassMaster(String classCode, String className, Boolean isRiskAsset,
                              Boolean allowMulti, BigDecimal avgReturn3y, Integer displayOrder) {
        this.classCode = classCode;
        this.className = className;
        this.isRiskAsset = isRiskAsset != null ? isRiskAsset : false;
        this.allowMulti = allowMulti != null ? allowMulti : false;
        this.avgReturn3y = avgReturn3y;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }
}
