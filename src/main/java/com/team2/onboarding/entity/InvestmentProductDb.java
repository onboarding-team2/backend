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
@Table(name = "investment_products_db")
@Checks({
        @Check(name = "chk_product_status",
                constraints = "status IN ('운용중', '만기완료')"),
        @Check(name = "chk_confirmed_amount",
                constraints = "status = '운용중' OR confirmed_amount IS NOT NULL")
})
public class InvestmentProductDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long principal;

    @Column(name = "annual_return_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal annualReturnRate;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Column(name = "confirmed_amount")
    private Long confirmedAmount;

    @Column(nullable = false, length = 10)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_master_id", nullable = false)
    private InvestmentProductMaster productMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_retirement_db_id", nullable = false)
    private CompanyRetirementDb companyRetirementDb;

    @Builder
    private InvestmentProductDb(Long principal, BigDecimal annualReturnRate, LocalDate purchaseDate,
            LocalDate maturityDate, Long confirmedAmount, String status,
            InvestmentProductMaster productMaster, CompanyRetirementDb companyRetirementDb) {
        this.principal = principal;
        this.annualReturnRate = annualReturnRate;
        this.purchaseDate = purchaseDate != null ? purchaseDate : LocalDate.now();
        this.maturityDate = maturityDate;
        this.confirmedAmount = confirmedAmount;
        this.status = status != null ? status : "운용중";
        this.productMaster = productMaster;
        this.companyRetirementDb = companyRetirementDb;
    }
}
