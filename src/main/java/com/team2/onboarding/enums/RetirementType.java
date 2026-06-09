package com.team2.onboarding.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RetirementType {
    MANDATORY("정년퇴직"),
    VOLUNTARY("희망퇴직");

    private final String description;
}
