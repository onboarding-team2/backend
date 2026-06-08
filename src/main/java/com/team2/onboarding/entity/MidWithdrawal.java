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
@Table(name = "mid_withdrawals")
public class MidWithdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_date")
    private LocalDate requestDate;

    private String status;

    private Long amount;

    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_retirement_id")
    private EmployeeRetirementDc employeeRetirementDc;

    @Builder
    private MidWithdrawal(LocalDate requestDate, String status, Long amount,
            String reason, EmployeeRetirementDc employeeRetirementDc) {
        this.requestDate = requestDate;
        this.status = status;
        this.amount = amount;
        this.reason = reason;
        this.employeeRetirementDc = employeeRetirementDc;
    }
}
