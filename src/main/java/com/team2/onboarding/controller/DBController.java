package com.team2.onboarding.controller;

import com.team2.onboarding.security.JwtTokenProvider;
import com.team2.onboarding.service.DBService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pension/db")
public class DBController {

    private final DBService dbService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/dashboard")
    public ResponseEntity<Object> getDashboard(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dbService.getDashboard(companyId));
    }

    @GetMapping("/members")
    public ResponseEntity<Object> getMembers(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dbService.getMembers(companyId));
    }

    @GetMapping("/deadlines")
    public ResponseEntity<Object> getDeadlines(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dbService.getDeadlines(companyId));
    }

    @GetMapping("/documents")
    public ResponseEntity<Object> getDocuments(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dbService.getDocuments(companyId));
    }

    private String extractCompanyId(String authHeader) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;
        return jwtTokenProvider.getCompanyIdFromToken(token);
    }
}
