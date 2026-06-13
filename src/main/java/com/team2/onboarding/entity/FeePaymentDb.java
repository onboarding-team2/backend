package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Checks;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "db_fee_payments",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_db_fee_per_year",
                columnNames = {"company_retirement_db_id", "payment_year", "fee_type"}
        )
)
@Checks({
        @Check(name = "chk_db_fee_status", constraints = "status IN ('납입완료', '미납')"),
        @Check(name = "chk_db_fee_type",   constraints = "fee_type IN ('운용관리', '자산관리')")
})
public class FeePaymentDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_year", nullable = false)
    private Integer paymentYear;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "fee_amount", nullable = false)
    private Long feeAmount;

    @Column(name = "fee_type", nullable = false, length = 20)
    private String feeType;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Column(nullable = false, length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_retirement_db_id", nullable = false)
    private CompanyRetirementDb companyRetirementDb;

    @Builder
    private FeePaymentDb(Integer paymentYear, LocalDate dueDate, Long feeAmount, String feeType,
            LocalDate paidDate, String status, CompanyRetirementDb companyRetirementDb) {
        this.paymentYear = paymentYear;
        this.dueDate = dueDate;
        this.feeAmount = feeAmount;
        this.feeType = feeType;
        this.paidDate = paidDate;
        this.status = status;
        this.companyRetirementDb = companyRetirementDb;
    }
}
