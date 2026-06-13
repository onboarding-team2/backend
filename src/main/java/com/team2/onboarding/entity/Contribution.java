package com.team2.onboarding.entity;

import com.team2.onboarding.enums.PaymentCycle;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dc_contributions")
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contribution_amount")
    private Long contributionAmount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    private String status;

    @Enumerated(EnumType.STRING)
    private PaymentCycle cycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_retirement_dc_id")
    private CompanyRetirementDc companyRetirementDc;

    @Builder
    private Contribution(Long contributionAmount, LocalDate dueDate, LocalDate paidDate,
            String status, PaymentCycle cycle, CompanyRetirementDc companyRetirementDc) {
        this.contributionAmount = contributionAmount;
        this.dueDate = dueDate;
        this.paidDate = paidDate;
        this.status = status;
        this.cycle = cycle;
        this.companyRetirementDc = companyRetirementDc;
    }
}
