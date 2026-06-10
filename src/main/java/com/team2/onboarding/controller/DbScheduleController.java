package com.team2.onboarding.controller;

import com.team2.onboarding.dto.DbScheduleCreateRequestDto;
import com.team2.onboarding.dto.DbScheduleDetailResponseDto;
import com.team2.onboarding.dto.DbScheduleResponseDto;
import com.team2.onboarding.service.DbScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/db/schedules")
public class DbScheduleController {

    private final DbScheduleService dbScheduleService;

    @GetMapping
    public ResponseEntity<DbScheduleResponseDto> getSchedules(
            @AuthenticationPrincipal String companyId,
            @RequestParam(name = "period", required = false) Integer period,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        return ResponseEntity.ok(dbScheduleService.getSchedules(Long.parseLong(companyId), period, keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DbScheduleDetailResponseDto> getScheduleDetail(
            @AuthenticationPrincipal String companyId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(dbScheduleService.getScheduleDetail(id, Long.parseLong(companyId)));
    }

    @PostMapping
    public ResponseEntity<DbScheduleDetailResponseDto> createSchedule(
            @AuthenticationPrincipal String companyId,
            @RequestBody DbScheduleCreateRequestDto request
    ) {
        return ResponseEntity.ok(dbScheduleService.createSchedule(Long.parseLong(companyId), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @AuthenticationPrincipal String companyId,
            @PathVariable Long id
    ) {
        dbScheduleService.deleteSchedule(id, Long.parseLong(companyId));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<DbScheduleDetailResponseDto> completeSchedule(
            @AuthenticationPrincipal String companyId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(dbScheduleService.completeSchedule(id, Long.parseLong(companyId)));
    }
}
