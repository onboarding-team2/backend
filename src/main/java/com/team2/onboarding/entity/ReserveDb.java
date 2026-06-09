package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Checks;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "reserve_db",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_retirement_db_id", "base_date"})
)
@Checks({
        @Check(name = "chk_reserve_status", constraints = "status IN ('적정', '주의', '추가납입필요')"),
        @Check(name = "chk_shortfall",      constraints = "shortfall_amount >= 0")
})
public class ReserveDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate;

    @Column(name = "benefit_obligation", nullable = false)
    private Long benefitObligation;

    @Column(name = "min_reserve", nullable = false)
    private Long minReserve;

    @Column(name = "funded_amount", nullable = false)
    private Long fundedAmount;

    @Column(name = "funding_ratio", nullable = false, precision = 8, scale = 2)
    private BigDecimal fundingRatio;

    @Column(name = "shortfall_amount", nullable = false)
    private Long shortfallAmount;

    @Column(name = "additional_due_date")
    private LocalDate additionalDueDate;

    @Column(nullable = false, length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_retirement_db_id", nullable = false)
    private CompanyRetirementDb companyRetirementDb;

    @Builder
    private ReserveDb(LocalDate baseDate, Long benefitObligation, Long minReserve, Long fundedAmount,
            BigDecimal fundingRatio, Long shortfallAmount, LocalDate additionalDueDate,
            String status, CompanyRetirementDb companyRetirementDb) {
        this.baseDate = baseDate;
        this.benefitObligation = benefitObligation;
        this.minReserve = minReserve;
        this.fundedAmount = fundedAmount;
        this.fundingRatio = fundingRatio;
        this.shortfallAmount = shortfallAmount != null ? shortfallAmount : 0L;
        this.additionalDueDate = additionalDueDate;
        this.status = status;
        this.companyRetirementDb = companyRetirementDb;
    }
}
