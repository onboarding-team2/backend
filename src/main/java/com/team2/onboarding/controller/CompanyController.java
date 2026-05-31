package com.team2.onboarding.controller;

import com.team2.onboarding.dto.DcContributionStatusResponseDto;
import com.team2.onboarding.dto.ExpectedRetireeDto;
import com.team2.onboarding.security.JwtTokenProvider;
import com.team2.onboarding.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final JwtTokenProvider jwtTokenProvider;
    private final DashboardService dashboardService;

    @GetMapping("/retirement-plan")
    public String retirementPlan() {
        return "회사퇴직연금제도 조회";
    }

    // DC형 부담금 납입 현황 (월별 납입금 + 미납 여부)
    @GetMapping("/dc-contributions")
    public ResponseEntity<DcContributionStatusResponseDto> getDcContributions(
            @RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dashboardService.getDcContributionStatus(companyId));
    }

    // 퇴직 예정자 목록
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
