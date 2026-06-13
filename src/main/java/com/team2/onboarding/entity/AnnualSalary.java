package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "dc_annual_salaries",
        uniqueConstraints = @UniqueConstraint(name = "uq_dc_salary_year_employee", columnNames = {"year", "employee_id"})
)
@Check(name = "chk_contribution_min", constraints = "contribution >= min_contribution")
public class AnnualSalary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4, nullable = false)
    private String year;

    @Column(nullable = false)
    private Long salary;

    @Column(name = "min_contribution", nullable = false)
    private Long minContribution;

    @Column(nullable = false)
    private Long contribution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Employee employee;

    @Builder
    private AnnualSalary(String year, Long salary, Long minContribution, Long contribution, Employee employee) {
        this.year = year;
        this.salary = salary;
        this.minContribution = minContribution;
        this.contribution = contribution != null ? contribution : 0L;
        this.employee = employee;
    }
}
