package com.team2.onboarding.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentCycle {

    YEARLY("연납"),
    QUARTERLY("분기납"),
    MONTHLY("월납");

    private final String description;
}