package com.team2.onboarding.entity;

import com.team2.onboarding.enums.ScheduleStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_retirement_id")
    private CompanyRetirement companyRetirement;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "schedule_employees",
            joinColumns = @JoinColumn(name = "schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> targetEmployees = new ArrayList<>();

    @Builder
    private Schedule(
            LocalDate dueDate,
            String title,
            String description,
            ScheduleStatus status,
            LocalDate createdDate,
            CompanyRetirement companyRetirement,
            List<Employee> targetEmployees
    ) {
        this.dueDate = dueDate;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
        this.companyRetirement = companyRetirement;
        this.targetEmployees = targetEmployees != null ? targetEmployees : new ArrayList<>();
    }

    public void complete() {
        this.status = ScheduleStatus.DONE;
    }

    public void markOverdue() {
        this.status = ScheduleStatus.OVERDUE;
    }
}
