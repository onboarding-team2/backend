package com.team2.onboarding.entity;

import com.team2.onboarding.enums.PlanType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "company_retirement_db")
public class CompanyRetirementDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_account")
    private String companyAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", length = 2)
    private PlanType planType;

    @Column(name = "contract_date")
    private LocalDate contractDate;

    @Column(name = "fiscal_month")
    private Integer fiscalMonth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Builder
    private CompanyRetirementDb(String companyAccount, PlanType planType, LocalDate contractDate,
            Integer fiscalMonth, Company company) {
        this.companyAccount = companyAccount;
        this.planType = planType != null ? planType : PlanType.DB;
        this.contractDate = contractDate;
        this.fiscalMonth = fiscalMonth != null ? fiscalMonth : 12;
        this.company = company;
    }
}
