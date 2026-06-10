package com.team2.onboarding.controller;

import com.team2.onboarding.dto.CompanyInfoDto;
import com.team2.onboarding.dto.DcContributionStatusResponseDto;
import com.team2.onboarding.dto.ExpectedRetireeDto;
import com.team2.onboarding.security.JwtTokenProvider;
import com.team2.onboarding.service.CompanyService;
import com.team2.onboarding.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;
    private final JwtTokenProvider jwtTokenProvider;
    private final DashboardService dashboardService;

    @GetMapping("/profile")
    public ResponseEntity<CompanyInfoDto> getMyCompanyInfo(
            @RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(companyService.getCompanyInfo(Long.parseLong(companyId)));
    }

    @GetMapping("/retirement-plan")
    public String retirementPlan() {
        return "회사퇴직연금제도 조회";
    }

    @GetMapping("/{companyId}/employee-count")
    public Long getEmployeeCount(@PathVariable Long companyId) {
        return companyService.getEmployeeCount(companyId);
    }

    @GetMapping("/{companyId}/dc-amount")
    public Long getDcAmount(@PathVariable Long companyId) {
        return companyService.getDcAmount(companyId);
    }

    @GetMapping("/{companyId}/defaultOption-nonEmployee-count")
    public ResponseEntity<Long> getDefaultOptionNonEmployeeCount(@PathVariable Long companyId) {
        return ResponseEntity.ok(companyService.getDefaultOptionNonEmployeeCount(companyId));
    }

    @GetMapping("/dc-contributions")
    public ResponseEntity<DcContributionStatusResponseDto> getDcContributions(
            @RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(companyService.getDcContributions(companyId));
    }

    @GetMapping("/expected-retirees")
    public ResponseEntity<List<ExpectedRetireeDto>> getExpectedRetirees(
            @RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dashboardService.getExpectedRetirees(companyId));
    }

    private String extractCompanyId(String authHeader) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;
        return jwtTokenProvider.getCompanyIdFromToken(token);
    }
}
