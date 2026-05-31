package com.team2.onboarding.entity;

import com.team2.onboarding.enums.EmployeeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "employees_retirement")
public class EmployeeRetirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_account")
    private String employeeAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Employee employee;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "default_option")
    private Boolean defaultOption;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type")
    private EmployeeType employeeType;

    private Long balance;
}