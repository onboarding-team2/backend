package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team2.onboarding.entity.ScheduleDc;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Builder
public class ScheduleDcResponseDto {

    @JsonProperty("total_count")
    private long totalCount;

    @JsonProperty("imminent_count")
    private long imminentCount;

    @JsonProperty("overdue_count")
    private long overdueCount;

    private List<ScheduleDcItemDto> schedules;

    @Getter
    @Builder
    public static class ScheduleDcItemDto {
        private Long id;
        private String title;

        @JsonProperty("due_date")
        private LocalDate dueDate;

        private String status;

        @JsonProperty("d_day")
        private String dDay;
    }

    public static ScheduleDcResponseDto of(List<ScheduleDc> scheduleList) {
        LocalDate today = LocalDate.now();

        List<ScheduleDcItemDto> items = scheduleList.stream()
                .map(s -> {
                    long days = ChronoUnit.DAYS.between(today, s.getDueDate());
                    String dDay;
                    if ("완료".equals(s.getStatus())) {
                        dDay = "완료";
                    } else if (days < 0) {
                        dDay = "D+" + Math.abs(days);
                    } else if (days == 0) {
                        dDay = "D-Day";
                    } else {
                        dDay = "D-" + days;
                    }

                    return ScheduleDcItemDto.builder()
                            .id(s.getId())
                            .title(s.getTitle())
                            .dueDate(s.getDueDate())
                            .status(s.getStatus())
                            .dDay(dDay)
                            .build();
                })
                .toList();

        long totalCount = items.stream()
                .filter(i -> !"완료".equals(i.getStatus()))
                .count();

        long imminentCount = scheduleList.stream()
                .filter(s -> !"완료".equals(s.getStatus()))
                .filter(s -> {
                    long days = ChronoUnit.DAYS.between(today, s.getDueDate());
                    return days >= 0 && days <= 14;
                })
                .count();

        long overdueCount = scheduleList.stream()
                .filter(s -> !"완료".equals(s.getStatus()))
                .filter(s -> s.getDueDate().isBefore(today))
                .count();

        return ScheduleDcResponseDto.builder()
                .totalCount(totalCount)
                .imminentCount(imminentCount)
                .overdueCount(overdueCount)
                .schedules(items)
                .build();
    }
}
