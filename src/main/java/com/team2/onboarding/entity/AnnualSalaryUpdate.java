package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "annual_salary_updates")
public class AnnualSalaryUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_year", length = 4)
    private String targetYear;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    private LocalDate deadline;

    private String status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annual_salary_id")
    private AnnualSalary annualSalary;

    @Builder
    private AnnualSalaryUpdate(String targetYear, LocalDateTime notifiedAt, LocalDate deadline,
            String status, LocalDateTime completedAt, AnnualSalary annualSalary) {
        this.targetYear = targetYear;
        this.notifiedAt = notifiedAt;
        this.deadline = deadline;
        this.status = status;
        this.completedAt = completedAt;
        this.annualSalary = annualSalary;
    }
}
