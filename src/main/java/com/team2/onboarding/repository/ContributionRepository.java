package com.team2.onboarding.repository;

import com.team2.onboarding.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributionRepository
        extends JpaRepository<Contribution, String> {

}