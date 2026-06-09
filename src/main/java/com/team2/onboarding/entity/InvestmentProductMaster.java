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
@Table(name = "investment_product_master")
@Check(name = "chk_master_category",
        constraints = "product_category IN ('정기예금', '이율보증형보험', 'ELB 및 ELD')")
public class InvestmentProductMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_provider", nullable = false)
    private String productProvider;

    @Column(name = "product_category", nullable = false, length = 30)
    private String productCategory;

    @Column(name = "is_principal_guaranteed", nullable = false)
    private Boolean isPrincipalGuaranteed;

    @Column(name = "annual_return_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal annualReturnRate;

    @Builder
    private InvestmentProductMaster(String productProvider, String productCategory,
            Boolean isPrincipalGuaranteed, BigDecimal annualReturnRate) {
        this.productProvider = productProvider;
        this.productCategory = productCategory;
        this.isPrincipalGuaranteed = isPrincipalGuaranteed != null ? isPrincipalGuaranteed : false;
        this.annualReturnRate = annualReturnRate;
    }
}
