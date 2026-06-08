package com.team2.onboarding.controller;

import com.team2.onboarding.dto.DcMemberItemDto;
import com.team2.onboarding.security.JwtTokenProvider;
import com.team2.onboarding.service.DCService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pension/dc")
public class DCController {

    private final DCService dcService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/dashboard")
    public ResponseEntity<Object> getDashboard(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dcService.getDashboard(companyId));
    }

    @GetMapping("/members")
    public ResponseEntity<List<DcMemberItemDto>> getMembers(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dcService.getMembers(companyId));
    }

    @GetMapping("/deadlines")
    public ResponseEntity<Object> getDeadlines(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dcService.getDeadlines(companyId));
    }

    @GetMapping("/documents")
    public ResponseEntity<Object> getDocuments(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dcService.getDocuments(companyId));
    }

    private String extractCompanyId(String authHeader) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;
        return jwtTokenProvider.getCompanyIdFromToken(token);
    }
}
