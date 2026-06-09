package com.team2.onboarding.controller;

import com.team2.onboarding.dto.ScheduleDcCreateRequestDto;
import com.team2.onboarding.dto.ScheduleDcDetailResponseDto;
import com.team2.onboarding.dto.ScheduleDcResponseDto;
import com.team2.onboarding.security.JwtTokenProvider;
import com.team2.onboarding.service.ScheduleDcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleDcController {

    private final ScheduleDcService scheduleDcService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ResponseEntity<ScheduleDcResponseDto> getSchedules(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(name = "period", required = false) Integer period,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        Long companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(scheduleDcService.getSchedules(companyId, period, keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDcDetailResponseDto> getScheduleDetail(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleDcService.getScheduleDetail(id));
    }

    @PostMapping
    public ResponseEntity<ScheduleDcDetailResponseDto> createSchedule(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ScheduleDcCreateRequestDto request
    ) {
        Long companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(scheduleDcService.createSchedule(companyId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleDcService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ScheduleDcDetailResponseDto> completeSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleDcService.completeSchedule(id));
    }

    private Long extractCompanyId(String authHeader) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;
        return Long.parseLong(jwtTokenProvider.getCompanyIdFromToken(token));
    }
}
