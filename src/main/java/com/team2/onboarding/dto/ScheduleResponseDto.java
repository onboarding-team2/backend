package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team2.onboarding.entity.Schedule;
import com.team2.onboarding.enums.ScheduleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Builder
public class ScheduleResponseDto {

    @JsonProperty("total_count")
    private long totalCount;

    @JsonProperty("imminent_count")
    private long imminentCount;

    @JsonProperty("overdue_count")
    private long overdueCount;

    private List<ScheduleItemDto> schedules;

    @Getter
    @Builder
    public static class ScheduleItemDto {
        private Long id;
        private String title;

        @JsonProperty("due_date")
        private LocalDate dueDate;

        private String status;

        @JsonProperty("d_day")
        private String dDay;
    }

    public static ScheduleResponseDto of(List<Schedule> scheduleList) {
        LocalDate today = LocalDate.now();

        List<ScheduleItemDto> items = scheduleList.stream()
                .map(s -> {
                    long days = ChronoUnit.DAYS.between(today, s.getDueDate());
                    String dDay;
                    if (s.getStatus() == ScheduleStatus.DONE) {
                        dDay = "완료";
                    } else if (days < 0) {
                        dDay = Math.abs(days) + "일 초과";
                    } else if (days == 0) {
                        dDay = "D-Day";
                    } else {
                        dDay = "D-" + days;
                    }

                    return ScheduleItemDto.builder()
                            .id(s.getId())
                            .title(s.getTitle())
                            .dueDate(s.getDueDate())
                            .status(s.getStatus().name())
                            .dDay(dDay)
                            .build();
                })
                .toList();

        long totalCount = items.stream()
                .filter(i -> !"DONE".equals(i.getStatus()))
                .count();

        long imminentCount = scheduleList.stream()
                .filter(s -> s.getStatus() != ScheduleStatus.DONE)
                .filter(s -> {
                    long days = ChronoUnit.DAYS.between(today, s.getDueDate());
                    return days >= 0 && days <= 14;
                })
                .count();

        long overdueCount = scheduleList.stream()
                .filter(s -> s.getStatus() == ScheduleStatus.OVERDUE
                        || (s.getStatus() != ScheduleStatus.DONE
                        && s.getDueDate().isBefore(today)))
                .count();

        return ScheduleResponseDto.builder()
                .totalCount(totalCount)
                .imminentCount(imminentCount)
                .overdueCount(overdueCount)
                .schedules(items)
                .build();
    }
}
