package com.team2.onboarding.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", unique = true)
    private String memberId;

    private String name;

    // TODO : 암호화
    private String rrn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Builder
    private Employee(
            String memberId,
            String name,
            String rrn,
            Company company
    ) {
        this.memberId = memberId;
        this.name = name;
        this.rrn = rrn;
        this.company = company;
    }
}