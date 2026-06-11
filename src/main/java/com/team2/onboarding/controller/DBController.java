package com.team2.onboarding.controller;

import com.team2.onboarding.dto.*;
import com.team2.onboarding.security.JwtTokenProvider;
import com.team2.onboarding.service.AssetsService;
import com.team2.onboarding.service.DBService;
import com.team2.onboarding.service.EmployeeService;
import com.team2.onboarding.service.InvestmentProductDbService;
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
    private final InvestmentProductDbService investmentProductDbService;
    private final AssetsService assetsService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        try {
            DBDashboardResponseDto result = dbService.getDashboard(companyId);
            if (result == null) return ResponseEntity.ok("null result");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @GetMapping("/members")
    public ResponseEntity<PageResponse<DbMemberItemDto>> getMembers(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) List<String> type,
            @RequestParam(required = false) List<String> irp,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(
                dbService.getMembers(companyId, name, status, type, irp, page, size)
        );
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<EmployeeDetailResponseDto> getMemberDetail(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(employeeService.getDbMemberDetail(companyId, id));
    }

    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioResponseDto> getPortfolio(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(investmentProductDbService.getPortfolio(companyId));
    }

    @GetMapping("/schedules")
    public ResponseEntity<Object> getSchedules(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dbService.getSchedules(companyId));
    }

    @GetMapping("/documents")
    public ResponseEntity<Object> getDocuments(@RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(dbService.getDocuments(companyId));
    }

    // ── 자산 운용 ────────────────────────────────────────────────────────────

    @GetMapping("/assets/current")
    public ResponseEntity<AssetsCurrentResponseDto> getAssetsCurrent(
            @RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(assetsService.getCurrent(companyId));
    }

    @PatchMapping("/assets/target-rate")
    public ResponseEntity<Double> updateTargetRate(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateTargetRateRequestDto req) {
        String companyId = extractCompanyId(authHeader);
        double newRate = assetsService.updateTargetReturnRate(companyId, req.getTargetReturnRate());
        return ResponseEntity.ok(newRate);
    }

    @GetMapping("/assets/simulation-options")
    public ResponseEntity<SimulationOptionsDto> getSimulationOptions() {
        return ResponseEntity.ok(assetsService.getSimulationOptions());
    }

    @PostMapping("/assets/simulation")
    public ResponseEntity<SimulationResponseDto> saveSimulation(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SimulationSaveRequestDto req) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(assetsService.saveSimulation(companyId, req));
    }

    @GetMapping("/assets/simulation")
    public ResponseEntity<List<SimulationResponseDto>> listSimulations(
            @RequestHeader("Authorization") String authHeader) {
        String companyId = extractCompanyId(authHeader);
        return ResponseEntity.ok(assetsService.listSimulations(companyId));
    }

    @DeleteMapping("/assets/simulation/{id}")
    public ResponseEntity<Void> deleteSimulation(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        String companyId = extractCompanyId(authHeader);
        assetsService.deleteSimulation(companyId, id);
        return ResponseEntity.noContent().build();
    }

    private String extractCompanyId(String authHeader) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;
        return jwtTokenProvider.getCompanyIdFromToken(token);
    }
}
