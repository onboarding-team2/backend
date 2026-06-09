package com.team2.onboarding.repository;

import com.team2.onboarding.entity.InvestmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentProductRepository
        extends JpaRepository<InvestmentProduct, String> {

}