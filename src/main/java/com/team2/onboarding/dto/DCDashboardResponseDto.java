package com.team2.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DCDashboardResponseDto {

    @JsonProperty("total_balance")
    private long totalBalance;

    @JsonProperty("total_employee")
    private long totalEmployee;

    @JsonProperty("default_option_not_selected")
    private long defaultOptionNotSelected;

    @JsonProperty("this_month_contribution")
    private long thisMonthContribution;

    @JsonProperty("contribution_due_date")
    private String contributionDueDate;
}
