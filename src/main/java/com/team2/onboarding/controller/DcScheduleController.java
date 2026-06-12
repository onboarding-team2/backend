package com.team2.onboarding.controller;

import com.team2.onboarding.dto.DcScheduleCreateRequestDto;
import com.team2.onboarding.dto.DcScheduleDetailResponseDto;
import com.team2.onboarding.dto.DcScheduleResponseDto;
import com.team2.onboarding.dto.DcScheduleUpdateRequestDto;
import com.team2.onboarding.service.DcScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dc/schedules")
public class DcScheduleController {

    private final DcScheduleService dcScheduleService;

    @GetMapping
    public ResponseEntity<DcScheduleResponseDto> getSchedules(
            @AuthenticationPrincipal String companyId,
            @RequestParam(name = "period", required = false) Integer period,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        return ResponseEntity.ok(dcScheduleService.getSchedules(Long.parseLong(companyId), period, keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DcScheduleDetailResponseDto> getScheduleDetail(
            @AuthenticationPrincipal String companyId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(dcScheduleService.getScheduleDetail(id, Long.parseLong(companyId)));
    }

    @PostMapping
    public ResponseEntity<DcScheduleDetailResponseDto> createSchedule(
            @AuthenticationPrincipal String companyId,
            @RequestBody DcScheduleCreateRequestDto request
    ) {
        return ResponseEntity.ok(dcScheduleService.createSchedule(Long.parseLong(companyId), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @AuthenticationPrincipal String companyId,
            @PathVariable Long id
    ) {
        dcScheduleService.deleteSchedule(id, Long.parseLong(companyId));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DcScheduleDetailResponseDto> updateSchedule(
            @AuthenticationPrincipal String companyId,
            @PathVariable Long id,
            @RequestBody DcScheduleUpdateRequestDto request
    ) {
        return ResponseEntity.ok(dcScheduleService.updateSchedule(id, Long.parseLong(companyId), request));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<DcScheduleDetailResponseDto> completeSchedule(
            @AuthenticationPrincipal String companyId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(dcScheduleService.completeSchedule(id, Long.parseLong(companyId)));
    }
}
