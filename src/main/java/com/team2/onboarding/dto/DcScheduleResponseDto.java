package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team2.onboarding.entity.DcSchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Builder
public class DcScheduleResponseDto {

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

        @JsonProperty("d_day")  // JPA 네이밍컨벤션으로 인해 JSON 중복 key 발생을 막기 위한 변수명 설정
        private String dayCount;

        private boolean required;
    }

    public static DcScheduleResponseDto of(List<DcSchedule> scheduleList) {
        LocalDate today = LocalDate.now();

        List<ScheduleDcItemDto> items = scheduleList.stream()
                .map(s -> {
                    long days = ChronoUnit.DAYS.between(today, s.getDueDate());
                    String dayCount;
                    if ("DONE".equals(s.getStatus())) {
                        dayCount = "완료";
                    } else if (days < 0) {
                        dayCount = "D+" + Math.abs(days);
                    } else if (days == 0) {
                        dayCount = "D-Day";
                    } else {
                        dayCount = "D-" + days;
                    }
                    return ScheduleDcItemDto.builder()
                            .id(s.getId())
                            .title(s.getTitle())
                            .dueDate(s.getDueDate())
                            .status(s.getStatus())
                            .dayCount(dayCount)
                            .required(s.isRequired())
                            .build();
                })
                .toList();

        long totalCount = items.stream()
                .filter(i -> !"DONE".equals(i.getStatus()))
                .count();

        long imminentCount = scheduleList.stream()
                .filter(s -> !"DONE".equals(s.getStatus()))
                .filter(s -> {
                    long days = ChronoUnit.DAYS.between(today, s.getDueDate());
                    return days >= 0 && days <= 14;
                })
                .count();

        long overdueCount = scheduleList.stream()
                .filter(s -> !"DONE".equals(s.getStatus()) && s.getDueDate().isBefore(today))
                .count();

        return DcScheduleResponseDto.builder()
                .totalCount(totalCount)
                .imminentCount(imminentCount)
                .overdueCount(overdueCount)
                .schedules(items)
                .build();
    }
}
