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
@Table(name = "irp_accounts")
public class IrpAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "opened_date")
    private LocalDate openedDate;

    private String status;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "delay_status")
    private String delayStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_retirement_id")
    private EmployeeRetirementDc employeeRetirementDc;

    @Builder
    private IrpAccount(LocalDate openedDate, String status, LocalDate terminationDate,
            String delayStatus, EmployeeRetirementDc employeeRetirementDc) {
        this.openedDate = openedDate;
        this.status = status;
        this.terminationDate = terminationDate;
        this.delayStatus = delayStatus;
        this.employeeRetirementDc = employeeRetirementDc;
    }
}
