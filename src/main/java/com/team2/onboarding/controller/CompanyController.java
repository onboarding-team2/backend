package com.team2.onboarding.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    @GetMapping("/retirement-plan")
    public String retirementPlan() {
        return "회사퇴직연금제도 조회";
    }
}