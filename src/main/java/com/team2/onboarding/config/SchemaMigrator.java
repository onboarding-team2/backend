package com.team2.onboarding.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchemaMigrator implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute(
            "ALTER TABLE employee_retirement_dc MODIFY COLUMN has_irp_account ENUM('Y','N') NOT NULL DEFAULT 'N'"
        );
        jdbcTemplate.execute(
            "ALTER TABLE employee_retirement_dc MODIFY COLUMN default_option ENUM('Y','N') NULL"
        );
        jdbcTemplate.execute(
            "ALTER TABLE company MODIFY COLUMN plan_type ENUM('DB','DC')"
        );
        jdbcTemplate.execute(
            "ALTER TABLE company_retirement_dc MODIFY COLUMN plan_type ENUM('DB','DC')"
        );
    }
}
