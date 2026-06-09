package com.team2.onboarding.controller;

import com.team2.onboarding.dto.ScheduleDbCreateRequestDto;
import com.team2.onboarding.dto.ScheduleDbDetailResponseDto;
import com.team2.onboarding.dto.ScheduleDbResponseDto;
import com.team2.onboarding.security.JwtTokenProvider;
import com.team2.onboarding.service.ScheduleDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules/db")
public class ScheduleDbController {

    private final ScheduleDbService scheduleDbService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ResponseEntity<ScheduleDbResponseDto> getSchedules(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(name = "period", required = false) Integer period,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        Long companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(scheduleDbService.getSchedules(companyId, period, keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDbDetailResponseDto> getScheduleDetail(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleDbService.getScheduleDetail(id));
    }

    @PostMapping
    public ResponseEntity<ScheduleDbDetailResponseDto> createSchedule(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ScheduleDbCreateRequestDto request
    ) {
        Long companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(scheduleDbService.createSchedule(companyId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleDbService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ScheduleDbDetailResponseDto> completeSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleDbService.completeSchedule(id));
    }

    private Long extractCompanyId(String authHeader) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;
        return Long.parseLong(jwtTokenProvider.getCompanyIdFromToken(token));
    }
}
