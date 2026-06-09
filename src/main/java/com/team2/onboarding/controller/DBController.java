package com.team2.onboarding.controller;

import com.team2.onboarding.dto.DbMemberItemDto;
import com.team2.onboarding.dto.EmployeeDetailResponseDto;
import com.team2.onboarding.security.JwtTokenProvider;
import com.team2.onboarding.service.DBService;
import com.team2.onboarding.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pension/db")
public class DBController {

    private final DBService dbService;
    private final EmployeeService employeeService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/dashboard")
    public ResponseEntity<Object> getDashboard(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dbService.getDashboard(companyId));
    }

    @GetMapping("/members")
    public ResponseEntity<List<DbMemberItemDto>> getMembers(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dbService.getMembers(companyId));
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<EmployeeDetailResponseDto> getMemberDetail(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(employeeService.getDbMemberDetail(companyId, id));
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
