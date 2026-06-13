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
@Table(name = "db_investment_product_master")
public class InvestmentProductMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_provider", nullable = false)
    private String productProvider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_class_id", nullable = false)
    private AssetClassMaster assetClass;

    @Column(name = "product_category", nullable = false, length = 30)
    private String productCategory;

    @Column(name = "is_principal_guaranteed", nullable = false)
    private Boolean isPrincipalGuaranteed;

    @Column(name = "annual_return_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal annualReturnRate;

    @Column(name = "return_rate_3y", precision = 5, scale = 2)
    private BigDecimal returnRate3y;

    @Column(name = "fee_rate", precision = 7, scale = 4)
    private BigDecimal feeRate;

    @Column(name = "product_tag", length = 20)
    private String productTag;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @Builder
    private InvestmentProductMaster(String productName, String productProvider, AssetClassMaster assetClass,
            String productCategory, Boolean isPrincipalGuaranteed, BigDecimal annualReturnRate,
            BigDecimal returnRate3y, BigDecimal feeRate, String productTag, Boolean isDefault) {
        this.productName = productName;
        this.productProvider = productProvider;
        this.assetClass = assetClass;
        this.productCategory = productCategory;
        this.isPrincipalGuaranteed = isPrincipalGuaranteed != null ? isPrincipalGuaranteed : false;
        this.annualReturnRate = annualReturnRate;
        this.returnRate3y = returnRate3y;
        this.feeRate = feeRate;
        this.productTag = productTag;
        this.isDefault = isDefault != null ? isDefault : false;
    }
}
