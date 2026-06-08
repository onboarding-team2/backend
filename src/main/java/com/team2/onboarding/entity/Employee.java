package com.team2.onboarding.entity;

import com.team2.onboarding.enums.EmployeeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 13)
    private String rrn;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type")
    private EmployeeType employeeType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Builder
    private Employee(String name, String rrn, EmployeeType employeeType,
            LocalDate startDate, LocalDate terminationDate, Company company) {
        this.name = name;
        this.rrn = rrn;
        this.employeeType = employeeType;
        this.startDate = startDate;
        this.terminationDate = terminationDate;
        this.company = company;
    }
}
