package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "contributions")
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contribution_amount")
    private Long contributionAmount;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Builder
    private Contribution(
            Long contributionAmount,
            LocalDate paidDate,
            Company company
    ) {
        this.contributionAmount = contributionAmount;
        this.paidDate = paidDate;
        this.company = company;
    }
}