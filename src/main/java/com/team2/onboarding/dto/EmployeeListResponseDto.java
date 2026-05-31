package com.team2.onboarding.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/** 가입자 현황 조회 응답: 총 가입자 수 + 페이지 목록. */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeListResponseDto {

    private long totalCount;
    private int page;
    private int size;
    private List<EmployeeListItemDto> members;

    public EmployeeListResponseDto(
            long totalCount,
            int page,
            int size,
            List<EmployeeListItemDto> members
    ) {
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;
        this.members = members;
    }
}
