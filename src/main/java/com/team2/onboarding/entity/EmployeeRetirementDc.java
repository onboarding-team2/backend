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
@Table(name = "employee_retirement_dc")
public class EmployeeRetirementDc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_account")
    private String employeeAccount;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "default_option")
    private String defaultOption;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type")
    private EmployeeType employeeType;

    private Long balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_retirement_dc_id")
    private CompanyRetirementDc companyRetirementDc;

    @Builder
    private EmployeeRetirementDc(String employeeAccount, String accountType, LocalDate joinDate,
            LocalDate startDate, LocalDate terminationDate, LocalDate effectiveDate,
            String defaultOption, EmployeeType employeeType, Long balance,
            Employee employee, CompanyRetirementDc companyRetirementDc) {
        this.employeeAccount = employeeAccount;
        this.accountType = accountType != null ? accountType : "DC";
        this.joinDate = joinDate;
        this.startDate = startDate;
        this.terminationDate = terminationDate;
        this.effectiveDate = effectiveDate;
        this.defaultOption = defaultOption;
        this.employeeType = employeeType;
        this.balance = balance;
        this.employee = employee;
        this.companyRetirementDc = companyRetirementDc;
    }
}
