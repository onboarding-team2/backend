package com.team2.onboarding.controller;

import com.team2.onboarding.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/retirement-plan")
    public String retirementPlan() {
        return "회사퇴직연금제도 조회";
    }

    // 해당 companyId에 재직중인 사람 수 (총 가입자 수)
    @GetMapping("/{companyId}/employee-count")
    public Long getEmployeeCount(
            @PathVariable Long companyId
    ) {
        return companyService.getEmployeeCount(companyId);
    }
}