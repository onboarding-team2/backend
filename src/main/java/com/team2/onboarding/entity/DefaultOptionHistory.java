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
@Table(name = "default_option_history")
public class DefaultOptionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "selected_option")
    private String selectedOption;

    @Column(name = "selected_date")
    private LocalDate selectedDate;

    private String status;

    @Column(name = "overdue_days")
    private int overdueDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_retirement_id")
    private EmployeeRetirementDc employeeRetirementDc;

    @Builder
    private DefaultOptionHistory(String selectedOption, LocalDate selectedDate,
            String status, int overdueDays, EmployeeRetirementDc employeeRetirementDc) {
        this.selectedOption = selectedOption;
        this.selectedDate = selectedDate;
        this.status = status;
        this.overdueDays = overdueDays;
        this.employeeRetirementDc = employeeRetirementDc;
    }
}
