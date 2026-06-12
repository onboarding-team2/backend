package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "schedules_dc")
@Check(name = "chk_dc_schedule_status", constraints = "status IN ('ACTIVE', 'DONE', 'OVERDUE')")
public class DcSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private String title;

    @Column(length = 500, nullable = false)
    private String description;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(nullable = false)
    private boolean required;

    @Column(nullable = false)
    private boolean done = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "dc_schedule_employees",
            joinColumns = @JoinColumn(name = "schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> targetEmployees = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Company company;

    @Builder
    private DcSchedule(
            LocalDate dueDate,
            String title,
            String description,
            String status,
            Boolean isMandatory,
            LocalDate createdDate,
            boolean required,
            List<Employee> targetEmployees,
            Company company
    ) {
        this.dueDate = dueDate;
        this.title = title;
        this.description = description;
        this.status = status;
        this.isMandatory = isMandatory;
        this.createdDate = createdDate != null ? createdDate : LocalDate.now();
        this.required = required;
        this.targetEmployees = targetEmployees != null ? targetEmployees : new ArrayList<>();
        this.company = company;
    }

    public void complete() {
        this.status = "DONE";
        this.done = true;
    }
}
