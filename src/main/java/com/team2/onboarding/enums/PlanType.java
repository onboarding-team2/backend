package com.team2.onboarding.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlanType {

    DB("확정급여형"),
    DC("확정기여형"),
    IRP("개인형퇴직연금");

    private final String description;
}