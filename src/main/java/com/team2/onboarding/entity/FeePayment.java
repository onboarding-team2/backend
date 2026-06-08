package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fee_payments")
public class FeePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fee_amount")
    private Long feeAmount;

    @Column(name = "fee_type")
    private String feeType;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_retirement_dc_id")
    private CompanyRetirementDc companyRetirementDc;

    @Builder
    private FeePayment(Long feeAmount, String feeType, LocalDate dueDate,
            LocalDate paidDate, String status, CompanyRetirementDc companyRetirementDc) {
        this.feeAmount = feeAmount;
        this.feeType = feeType;
        this.dueDate = dueDate;
        this.paidDate = paidDate;
        this.status = status;
        this.companyRetirementDc = companyRetirementDc;
    }
}
