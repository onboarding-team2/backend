package com.team2.onboarding.entity;

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

    @Column(name = "company_id", unique = true)
    private String companyId;

    private String brn;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "representative_name")
    private String representativeName;

    private String password;

    @Builder
    private Company(String companyId, String brn, String companyName, String representativeName, String password) {
        this.companyId = companyId;
        this.brn = brn;
        this.companyName = companyName;
        this.representativeName = representativeName;
        this.password = password;
    }

    // 💡 비밀번호 재설정을 위한 도메인 비즈니스 메서드 추가
    public void updatePassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }

    public void updateIdentifiers(String companyId, String brn) {
        this.companyId = companyId;
        this.brn = brn;
    }
}