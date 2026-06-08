package com.team2.onboarding.entity;

import com.team2.onboarding.enums.PaymentCycle;
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
@Table(name = "company_retirement_dc")
public class CompanyRetirementDc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_account")
    private String companyAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type")
    private PlanType planType;

    @Column(name = "contract_date")
    private LocalDate contractDate;

    @Column(name = "fee_due_date")
    private LocalDate feeDueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_cycle")
    private PaymentCycle paymentCycle;

    @Column(name = "contribution_due_date")
    private LocalDate contributionDueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Builder
    private CompanyRetirementDc(String companyAccount, PlanType planType, LocalDate contractDate,
            LocalDate feeDueDate, PaymentCycle paymentCycle, LocalDate contributionDueDate, Company company) {
        this.companyAccount = companyAccount;
        this.planType = planType;
        this.contractDate = contractDate;
        this.feeDueDate = feeDueDate;
        this.paymentCycle = paymentCycle;
        this.contributionDueDate = contributionDueDate;
        this.company = company;
    }
}
