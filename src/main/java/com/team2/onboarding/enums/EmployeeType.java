package com.team2.onboarding.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmployeeType {

    EXECUTIVE("임원"),
    EMPLOYEE("사원");

    private final String description;
}