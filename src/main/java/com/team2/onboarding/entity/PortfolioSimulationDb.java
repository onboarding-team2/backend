package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Checks;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "portfolio_simulation_db")
@Checks({
        @Check(name = "chk_sim_status",   constraints = "status IN ('임시저장', '확정', '폐기')"),
        @Check(name = "chk_risk_limit",   constraints = "risk_asset_ratio <= 100"),
})
public class PortfolioSimulationDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "simulation_name", length = 100)
    private String simulationName;

    @Column(name = "expected_return_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal expectedReturnRate;

    @Column(name = "risk_asset_ratio", precision = 5, scale = 2, nullable = false)
    private BigDecimal riskAssetRatio;

    @Column(name = "preset_type", length = 20)
    private String presetType;

    @Column(nullable = false, length = 10)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_retirement_db_id", nullable = false)
    private CompanyRetirementDb companyRetirementDb;

    @OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioSimulationItem> items = new ArrayList<>();

    @Builder
    private PortfolioSimulationDb(String simulationName, BigDecimal expectedReturnRate,
                                   BigDecimal riskAssetRatio, String presetType,
                                   String status, CompanyRetirementDb companyRetirementDb) {
        this.simulationName = simulationName;
        this.expectedReturnRate = expectedReturnRate;
        this.riskAssetRatio = riskAssetRatio;
        this.presetType = presetType != null ? presetType : "CUSTOM";
        this.status = status != null ? status : "임시저장";
        this.createdAt = LocalDateTime.now();
        this.companyRetirementDb = companyRetirementDb;
    }
}
