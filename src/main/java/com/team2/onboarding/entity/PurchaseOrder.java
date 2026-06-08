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
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "instruction_date")
    private LocalDate instructionDate;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_retirement_id")
    private EmployeeRetirementDc employeeRetirementDc;

    @Builder
    private PurchaseOrder(String productName, String productType, LocalDate instructionDate,
            LocalDate maturityDate, String status, EmployeeRetirementDc employeeRetirementDc) {
        this.productName = productName;
        this.productType = productType;
        this.instructionDate = instructionDate;
        this.maturityDate = maturityDate;
        this.status = status;
        this.employeeRetirementDc = employeeRetirementDc;
    }
}
