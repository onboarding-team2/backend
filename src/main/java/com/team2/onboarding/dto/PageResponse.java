package com.team2.onboarding.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 목록 조회 공용 페이지 응답.
 */
@Getter
@Builder
public class PageResponse<T> {

    private long totalCount;
    private int page;
    private int size;
    private int totalPages;
    private List<T> members;

    public static <T> PageResponse<T> of(List<T> all, int page, int size) {
        int total = all.size();
        int safeSize = size <= 0 ? total : size;
        int from = Math.min(page * safeSize, total);
        int to = Math.min(from + safeSize, total);
        List<T> items = from <= to ? all.subList(from, to) : List.of();
        int totalPages = safeSize == 0 ? 0 : (int) Math.ceil((double) total / safeSize);
        return PageResponse.<T>builder()
                .totalCount(total)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .members(items)
                .build();
    }
}
