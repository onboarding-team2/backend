package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "db_employee_retirement",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "company_retirement_db_id"})
)
public class EmployeeRetirementDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_type", nullable = false, length = 10)
    private String accountType;

    @Column(name = "has_irp_account", length = 10)
    private String hasIrpAccount;

    @Column(name = "employee_account", nullable = false)
    private String employeeAccount;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_retirement_db_id", nullable = false)
    private CompanyRetirementDb companyRetirementDb;

    @Builder
    private EmployeeRetirementDb(String accountType, String hasIrpAccount, String employeeAccount,
            LocalDate joinDate, Employee employee, CompanyRetirementDb companyRetirementDb) {
        this.accountType = accountType != null ? accountType : "DB";
        this.hasIrpAccount = hasIrpAccount != null ? hasIrpAccount : "N";
        this.employeeAccount = employeeAccount;
        this.joinDate = joinDate;
        this.employee = employee;
        this.companyRetirementDb = companyRetirementDb;
    }
}
