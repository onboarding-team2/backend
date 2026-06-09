package com.team2.onboarding.service;

import com.team2.onboarding.dto.MaturingProductDto;
import com.team2.onboarding.dto.PortfolioCategoryDto;
import com.team2.onboarding.dto.PortfolioResponseDto;
import com.team2.onboarding.entity.CompanyRetirementDb;
import com.team2.onboarding.entity.InvestmentProductDb;
import com.team2.onboarding.repository.CompanyRetirementDbRepository;
import com.team2.onboarding.repository.InvestmentProductDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvestmentProductDbService {

    private final InvestmentProductDbRepository investmentProductDbRepository;
    private final CompanyRetirementDbRepository companyRetirementDbRepository;

    public PortfolioResponseDto getPortfolio(String companyId) {
        Long id = Long.parseLong(companyId);
        CompanyRetirementDb companyRetirementDb = companyRetirementDbRepository
                .findByCompany_Id(id)
                .orElseThrow(() -> new IllegalArgumentException("DB 퇴직연금 계약 정보를 찾을 수 없습니다."));

        List<InvestmentProductDb> products = investmentProductDbRepository
                .findByCompanyRetirementDb_Id(companyRetirementDb.getId());

        if (products.isEmpty()) {
            return PortfolioResponseDto.builder()
                    .portfolioItems(List.of())
                    .maturingProducts(List.of())
                    .averageReturnRate(BigDecimal.ZERO)
                    .principalGuaranteedRatio(BigDecimal.ZERO)
                    .build();
        }

        Map<String, Long> categoryAmounts = new LinkedHashMap<>();
        categoryAmounts.put("정기예금", 0L);
        categoryAmounts.put("이율보증형보험", 0L);
        categoryAmounts.put("ELB 및 ELD", 0L);

        long totalAmount = 0;
        long guaranteedAmount = 0;
        double weightedRateSum = 0;

        for (InvestmentProductDb p : products) {
            long amount = evaluateAmount(p);
            String category = p.getProductMaster().getProductCategory();
            categoryAmounts.merge(category, amount, Long::sum);
            totalAmount += amount;
            weightedRateSum += p.getAnnualReturnRate().doubleValue() * amount;
            if (Boolean.TRUE.equals(p.getProductMaster().getIsPrincipalGuaranteed())) {
                guaranteedAmount += amount;
            }
        }

        final long finalTotal = totalAmount;

        List<PortfolioCategoryDto> portfolioItems = categoryAmounts.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(e -> PortfolioCategoryDto.builder()
                        .category(e.getKey())
                        .amount(e.getValue())
                        .percent(finalTotal > 0 ? Math.round(e.getValue() * 100.0 / finalTotal) : 0)
                        .build())
                .collect(Collectors.toList());

        List<MaturingProductDto> maturingProducts = products.stream()
                .filter(p -> "운용중".equals(p.getStatus()) && p.getMaturityDate() != null)
                .sorted(Comparator.comparing(InvestmentProductDb::getMaturityDate))
                .limit(3)
                .map(p -> MaturingProductDto.builder()
                        .name(p.getProductMaster().getProductProvider() + " " + p.getProductMaster().getProductCategory())
                        .maturityDate(p.getMaturityDate())
                        .principal(p.getPrincipal())
                        .evaluatedAmount(evaluateAmount(p))
                        .build())
                .collect(Collectors.toList());

        BigDecimal averageReturnRate = finalTotal > 0
                ? BigDecimal.valueOf(weightedRateSum / finalTotal).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal principalGuaranteedRatio = finalTotal > 0
                ? BigDecimal.valueOf(guaranteedAmount * 100.0 / finalTotal).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return PortfolioResponseDto.builder()
                .portfolioItems(portfolioItems)
                .maturingProducts(maturingProducts)
                .averageReturnRate(averageReturnRate)
                .principalGuaranteedRatio(principalGuaranteedRatio)
                .build();
    }

    private long evaluateAmount(InvestmentProductDb p) {
        if ("만기완료".equals(p.getStatus())) {
            return p.getConfirmedAmount();
        }
        return Math.round(p.getPrincipal() * (1 + p.getAnnualReturnRate().doubleValue() / 100.0));
    }
}
