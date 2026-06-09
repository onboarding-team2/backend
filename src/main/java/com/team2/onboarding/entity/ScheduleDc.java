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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "schedules_dc")
@Check(name = "chk_schedule_status", constraints = "status IN ('예정', '진행중', '완료')")
public class ScheduleDc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "target_employees", columnDefinition = "JSON DEFAULT (JSON_ARRAY())")
    private String targetEmployees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Company company;

    @Builder
    private ScheduleDc(LocalDate dueDate, String title, String description,
            String status, LocalDate createdDate, String targetEmployees, Company company) {
        this.dueDate = dueDate;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate != null ? createdDate : LocalDate.now();
        this.targetEmployees = targetEmployees;
        this.company = company;
    }

    public void complete() {
        this.status = "완료";
    }
}
