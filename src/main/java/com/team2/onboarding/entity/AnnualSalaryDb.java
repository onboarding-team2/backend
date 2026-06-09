package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "annual_salaries_db",
        uniqueConstraints = @UniqueConstraint(name = "uq_db_salary_year_employee", columnNames = {"year", "employee_id"})
)
public class AnnualSalaryDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4, nullable = false)
    private String year;

    @Column(nullable = false)
    private Long salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Employee employee;

    @Builder
    private AnnualSalaryDb(String year, Long salary, Employee employee) {
        this.year = year;
        this.salary = salary;
        this.employee = employee;
    }
}
