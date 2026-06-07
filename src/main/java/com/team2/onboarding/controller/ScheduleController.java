package com.team2.onboarding.controller;

import com.team2.onboarding.dto.ScheduleCreateRequestDto;
import com.team2.onboarding.dto.ScheduleDetailResponseDto;
import com.team2.onboarding.dto.ScheduleResponseDto;
import com.team2.onboarding.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<ScheduleResponseDto> getSchedules(
            @RequestParam(name = "company_id") Long companyId,
            @RequestParam(name = "period", required = false) Integer period,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        return ResponseEntity.ok(scheduleService.getSchedules(companyId, period, keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDetailResponseDto> getScheduleDetail(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleDetail(id));
    }

    @PostMapping
    public ResponseEntity<ScheduleDetailResponseDto> createSchedule(
            @RequestParam(name = "company_id") Long companyId,
            @RequestBody ScheduleCreateRequestDto request
    ) {
        return ResponseEntity.ok(scheduleService.createSchedule(companyId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ScheduleDetailResponseDto> completeSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.completeSchedule(id));
    }
}
