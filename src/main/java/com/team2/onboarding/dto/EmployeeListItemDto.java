package com.team2.onboarding.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** 가입자 목록의 한 행. */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeListItemDto {

    private Long id;
    private String memberId;
    private String name;
    private String position;          // 직위 (임원/사원) - EmployeeType.description
    private LocalDate joinDate;
    private String planType;          // DC/DB - 회사 단위 값
    private Long balance;
    private Boolean contributionPaid; // 회사 최근 부담금 납입 여부 (정의 합의 필요)
    private String status;            // "재직" / "퇴직"

    @Builder
    private EmployeeListItemDto(
            Long id,
            String memberId,
            String name,
            String position,
            LocalDate joinDate,
            String planType,
            Long balance,
            Boolean contributionPaid,
            String status
    ) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.position = position;
        this.joinDate = joinDate;
        this.planType = planType;
        this.balance = balance;
        this.contributionPaid = contributionPaid;
        this.status = status;
    }
}
