package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Checks;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "employee_retirement_dc")
@Checks({
        @Check(name = "chk_account_type",   constraints = "account_type IN ('DC')"),
        @Check(name = "chk_default_option", constraints = "default_option IN ('Y', 'N') OR default_option IS NULL")
})
public class EmployeeRetirementDc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_type", nullable = false, length = 10)
    private String accountType;

    @Column(name = "default_option", length = 10)
    private String defaultOption;

    @Column(name = "has_irp_account", length = 10)
    private String hasIrpAccount;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "employee_account", nullable = false)
    private String employeeAccount;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_retirement_dc_id", nullable = false)
    private CompanyRetirementDc companyRetirementDc;

    @Builder
    private EmployeeRetirementDc(String accountType, String defaultOption, String hasIrpAccount,
            LocalDate effectiveDate, String employeeAccount, LocalDate joinDate,
            Employee employee, CompanyRetirementDc companyRetirementDc) {
        this.accountType = accountType != null ? accountType : "DC";
        this.defaultOption = defaultOption != null ? defaultOption : "N";
        this.hasIrpAccount = hasIrpAccount != null ? hasIrpAccount : "N";
        this.effectiveDate = effectiveDate;
        this.employeeAccount = employeeAccount;
        this.joinDate = joinDate;
        this.employee = employee;
        this.companyRetirementDc = companyRetirementDc;
    }
}
