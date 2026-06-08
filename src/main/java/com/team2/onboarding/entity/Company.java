package com.team2.onboarding.entity;

import com.team2.onboarding.enums.PlanType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 10)
    private String brn;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "representative_name")
    private String representativeName;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", length = 2)
    private PlanType planType;

    @Builder
    private Company(String brn, String companyName, String representativeName,
            String password, PlanType planType) {
        this.brn = brn;
        this.companyName = companyName;
        this.representativeName = representativeName;
        this.password = password;
        this.planType = planType;
    }

    public void updatePassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }
}
