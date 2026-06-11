package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "return_history_db",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_retirement_db_id", "base_date"}))
public class ReturnHistoryDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate;

    @Column(name = "return_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal returnRate;

    @Column(name = "funded_amount", nullable = false)
    private Long fundedAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_retirement_db_id", nullable = false)
    private CompanyRetirementDb companyRetirementDb;

    @Builder
    private ReturnHistoryDb(LocalDate baseDate, BigDecimal returnRate, Long fundedAmount,
                             CompanyRetirementDb companyRetirementDb) {
        this.baseDate = baseDate;
        this.returnRate = returnRate;
        this.fundedAmount = fundedAmount;
        this.companyRetirementDb = companyRetirementDb;
    }
}
