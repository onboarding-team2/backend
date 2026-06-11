package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "portfolio_simulation_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"simulation_id", "asset_class_id", "product_master_id"}))
@Check(name = "chk_weight", constraints = "weight_pct > 0 AND weight_pct <= 100")
public class PortfolioSimulationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private PortfolioSimulationDb simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_class_id", nullable = false)
    private AssetClassMaster assetClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_master_id")
    private InvestmentProductMaster productMaster;

    @Column(name = "weight_pct", precision = 5, scale = 2, nullable = false)
    private BigDecimal weightPct;

    @Column(name = "applied_return", precision = 5, scale = 2, nullable = false)
    private BigDecimal appliedReturn;

    @Builder
    private PortfolioSimulationItem(PortfolioSimulationDb simulation, AssetClassMaster assetClass,
                                     InvestmentProductMaster productMaster,
                                     BigDecimal weightPct, BigDecimal appliedReturn) {
        this.simulation = simulation;
        this.assetClass = assetClass;
        this.productMaster = productMaster;
        this.weightPct = weightPct;
        this.appliedReturn = appliedReturn;
    }
}
