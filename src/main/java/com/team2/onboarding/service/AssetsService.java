package com.team2.onboarding.service;

import com.team2.onboarding.dto.*;
import com.team2.onboarding.entity.*;
import com.team2.onboarding.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetsService {

    private static final Map<String, String> CLASS_COLORS = Map.of(
            "DEPOSIT", "#2563eb",
            "BOND",    "#64748b",
            "MIXED",   "#6366f1",
            "DOM_EQ",  "#0ea5e9",
            "OVS_EQ",  "#06b6d4"
    );

    private static final Map<String, String> CLASS_DISPLAY_NAMES = Map.of(
            "DEPOSIT", "원리금보장형",
            "BOND",    "채권형",
            "MIXED",   "혼합형·TDF",
            "DOM_EQ",  "국내주식ETF",
            "OVS_EQ",  "해외주식ETF"
    );

    private final CompanyRetirementDbRepository companyRetirementDbRepository;
    private final ReturnHistoryDbRepository returnHistoryDbRepository;
    private final InvestmentProductDbRepository investmentProductDbRepository;
    private final AssetClassMasterRepository assetClassMasterRepository;
    private final InvestmentProductMasterRepository investmentProductMasterRepository;
    private final PortfolioSimulationDbRepository simulationDbRepository;

    @Transactional(readOnly = true)
    public AssetsCurrentResponseDto getCurrent(String companyId) {
        Long id = Long.parseLong(companyId);
        CompanyRetirementDb crd = companyRetirementDbRepository.findByCompany_Id(id)
                .orElseThrow(() -> new IllegalArgumentException("DB 계약 정보를 찾을 수 없습니다."));

        List<ReturnHistoryDb> history = returnHistoryDbRepository
                .findByCompanyRetirementDb_IdOrderByBaseDateAsc(crd.getId());

        double currentReturnRate = history.isEmpty() ? 0.0
                : history.get(history.size() - 1).getReturnRate().doubleValue();

        List<AssetsCurrentResponseDto.ReturnHistoryPoint> historyPoints = history.stream()
                .map(h -> AssetsCurrentResponseDto.ReturnHistoryPoint.builder()
                        .baseDate(h.getBaseDate().toString())
                        .returnRate(h.getReturnRate().doubleValue())
                        .build())
                .collect(Collectors.toList());

        List<InvestmentProductDb> products = investmentProductDbRepository
                .findByCompanyRetirementDb_Id(crd.getId());

        Map<String, Long> amountByCode = new LinkedHashMap<>();
        for (String code : List.of("DEPOSIT", "BOND", "MIXED", "DOM_EQ", "OVS_EQ")) {
            amountByCode.put(code, 0L);
        }

        long totalAmount = 0L;
        double riskAmount = 0.0;

        for (InvestmentProductDb p : products) {
            long evaluated = evaluateAmount(p);
            String code = p.getProductMaster().getAssetClass().getClassCode();
            amountByCode.merge(code, evaluated, Long::sum);
            totalAmount += evaluated;
            if (Boolean.TRUE.equals(p.getProductMaster().getAssetClass().getIsRiskAsset())) {
                riskAmount += evaluated;
            }
        }

        final long finalTotal = totalAmount;
        List<AssetsCurrentResponseDto.AssetClassPortfolio> portfolioByClass = amountByCode.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(e -> AssetsCurrentResponseDto.AssetClassPortfolio.builder()
                        .classCode(e.getKey())
                        .className(CLASS_DISPLAY_NAMES.getOrDefault(e.getKey(), e.getKey()))
                        .amount(e.getValue())
                        .pct(finalTotal > 0 ? Math.round(e.getValue() * 100.0 / finalTotal) : 0)
                        .color(CLASS_COLORS.getOrDefault(e.getKey(), "#94a3b8"))
                        .build())
                .collect(Collectors.toList());

        Map<String, List<InvestmentProductDb>> byCode = products.stream()
                .collect(Collectors.groupingBy(p -> p.getProductMaster().getAssetClass().getClassCode()));

        List<AssetsCurrentResponseDto.AssetClassHoldings> holdingsByClass = new ArrayList<>();
        for (String code : List.of("DEPOSIT", "BOND", "MIXED", "DOM_EQ", "OVS_EQ")) {
            List<InvestmentProductDb> grouped = byCode.getOrDefault(code, List.of());
            if (grouped.isEmpty()) continue;
            List<AssetsCurrentResponseDto.ProductHolding> productList = grouped.stream()
                    .map(p -> AssetsCurrentResponseDto.ProductHolding.builder()
                            .productName(p.getProductMaster().getProductName())
                            .provider(p.getProductMaster().getProductProvider())
                            .returnRate(p.getAnnualReturnRate().doubleValue())
                            .amount(evaluateAmount(p))
                            .build())
                    .collect(Collectors.toList());
            holdingsByClass.add(AssetsCurrentResponseDto.AssetClassHoldings.builder()
                    .classCode(code)
                    .className(CLASS_DISPLAY_NAMES.getOrDefault(code, code))
                    .color(CLASS_COLORS.getOrDefault(code, "#94a3b8"))
                    .products(productList)
                    .build());
        }

        double riskRatio = finalTotal > 0 ? riskAmount * 100.0 / finalTotal : 0.0;

        return AssetsCurrentResponseDto.builder()
                .currentReturnRate(currentReturnRate)
                .targetReturnRate(crd.getTargetReturnRate().doubleValue())
                .returnHistory(historyPoints)
                .portfolioByClass(portfolioByClass)
                .holdingsByClass(holdingsByClass)
                .totalAmount(finalTotal)
                .riskAssetRatio(Math.round(riskRatio * 10.0) / 10.0)
                .build();
    }

    @Transactional
    public double updateTargetReturnRate(String companyId, BigDecimal newRate) {
        Long id = Long.parseLong(companyId);
        CompanyRetirementDb crd = companyRetirementDbRepository.findByCompany_Id(id)
                .orElseThrow(() -> new IllegalArgumentException("DB 계약 정보를 찾을 수 없습니다."));
        crd.updateTargetReturnRate(newRate);
        companyRetirementDbRepository.save(crd);
        return newRate.doubleValue();
    }

    @Transactional(readOnly = true)
    public SimulationOptionsDto getSimulationOptions() {
        List<AssetClassMaster> classes = assetClassMasterRepository.findAll();
        classes.sort(Comparator.comparingInt(AssetClassMaster::getDisplayOrder));

        List<InvestmentProductMaster> allProducts = investmentProductMasterRepository.findAll();
        Map<Long, List<InvestmentProductMaster>> byClassId = allProducts.stream()
                .collect(Collectors.groupingBy(p -> p.getAssetClass().getId()));

        List<SimulationOptionsDto.AssetClassOption> options = classes.stream()
                .map(cls -> {
                    List<SimulationOptionsDto.ProductOption> products = byClassId
                            .getOrDefault(cls.getId(), List.of()).stream()
                            .map(p -> SimulationOptionsDto.ProductOption.builder()
                                    .id(p.getId())
                                    .name(p.getProductName())
                                    .returnRate(p.getAnnualReturnRate().doubleValue())
                                    .tag(p.getProductTag())
                                    .provider(p.getProductProvider())
                                    .build())
                            .collect(Collectors.toList());
                    return SimulationOptionsDto.AssetClassOption.builder()
                            .id(cls.getId())
                            .classCode(cls.getClassCode())
                            .className(CLASS_DISPLAY_NAMES.getOrDefault(cls.getClassCode(), cls.getClassName()))
                            .isRisk(Boolean.TRUE.equals(cls.getIsRiskAsset()))
                            .allowMulti(Boolean.TRUE.equals(cls.getAllowMulti()))
                            .avgReturn3y(cls.getAvgReturn3y() != null ? cls.getAvgReturn3y().doubleValue() : 0.0)
                            .color(CLASS_COLORS.getOrDefault(cls.getClassCode(), "#94a3b8"))
                            .products(products)
                            .build();
                })
                .collect(Collectors.toList());

        return SimulationOptionsDto.builder().classes(options).build();
    }

    @Transactional
    public SimulationResponseDto saveSimulation(String companyId, SimulationSaveRequestDto req) {
        Long id = Long.parseLong(companyId);
        CompanyRetirementDb crd = companyRetirementDbRepository.findByCompany_Id(id)
                .orElseThrow(() -> new IllegalArgumentException("DB 계약 정보를 찾을 수 없습니다."));

        PortfolioSimulationDb sim = PortfolioSimulationDb.builder()
                .simulationName(req.getSimulationName())
                .expectedReturnRate(req.getExpectedReturnRate())
                .riskAssetRatio(req.getRiskAssetRatio())
                .presetType(req.getPresetType())
                .status("임시저장")
                .companyRetirementDb(crd)
                .build();
        sim = simulationDbRepository.save(sim);

        if (req.getItems() != null) {
            for (SimulationSaveRequestDto.SimulationItemDto itemDto : req.getItems()) {
                AssetClassMaster ac = assetClassMasterRepository.findById(itemDto.getAssetClassId())
                        .orElseThrow();
                InvestmentProductMaster pm = itemDto.getProductMasterId() != null
                        ? investmentProductMasterRepository.findById(itemDto.getProductMasterId()).orElse(null)
                        : null;
                PortfolioSimulationItem item = PortfolioSimulationItem.builder()
                        .simulation(sim)
                        .assetClass(ac)
                        .productMaster(pm)
                        .weightPct(itemDto.getWeightPct())
                        .appliedReturn(itemDto.getAppliedReturn())
                        .build();
                sim.getItems().add(item);
            }
            sim = simulationDbRepository.save(sim);
        }

        return toResponseDto(sim);
    }

    @Transactional(readOnly = true)
    public List<SimulationResponseDto> listSimulations(String companyId) {
        Long id = Long.parseLong(companyId);
        CompanyRetirementDb crd = companyRetirementDbRepository.findByCompany_Id(id)
                .orElseThrow(() -> new IllegalArgumentException("DB 계약 정보를 찾을 수 없습니다."));
        return simulationDbRepository
                .findByCompanyRetirementDb_IdOrderByCreatedAtDesc(crd.getId())
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSimulation(String companyId, Long simulationId) {
        Long id = Long.parseLong(companyId);
        CompanyRetirementDb crd = companyRetirementDbRepository.findByCompany_Id(id)
                .orElseThrow(() -> new IllegalArgumentException("DB 계약 정보를 찾을 수 없습니다."));
        PortfolioSimulationDb sim = simulationDbRepository.findById(simulationId)
                .orElseThrow(() -> new IllegalArgumentException("시뮬레이션을 찾을 수 없습니다."));
        if (!sim.getCompanyRetirementDb().getId().equals(crd.getId())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        simulationDbRepository.delete(sim);
    }

    private SimulationResponseDto toResponseDto(PortfolioSimulationDb sim) {
        List<SimulationResponseDto.SimulationItemResponse> items = sim.getItems().stream()
                .map(item -> SimulationResponseDto.SimulationItemResponse.builder()
                        .assetClassId(item.getAssetClass().getId())
                        .classCode(item.getAssetClass().getClassCode())
                        .className(CLASS_DISPLAY_NAMES.getOrDefault(item.getAssetClass().getClassCode(), item.getAssetClass().getClassName()))
                        .productMasterId(item.getProductMaster() != null ? item.getProductMaster().getId() : null)
                        .productName(item.getProductMaster() != null ? item.getProductMaster().getProductName() : null)
                        .weightPct(item.getWeightPct().doubleValue())
                        .appliedReturn(item.getAppliedReturn().doubleValue())
                        .build())
                .collect(Collectors.toList());

        return SimulationResponseDto.builder()
                .id(sim.getId())
                .simulationName(sim.getSimulationName())
                .expectedReturnRate(sim.getExpectedReturnRate().doubleValue())
                .riskAssetRatio(sim.getRiskAssetRatio().doubleValue())
                .presetType(sim.getPresetType())
                .status(sim.getStatus())
                .createdAt(sim.getCreatedAt().toString())
                .items(items)
                .build();
    }

    private long evaluateAmount(InvestmentProductDb p) {
        if ("만기완료".equals(p.getStatus())) return p.getConfirmedAmount();
        return Math.round(p.getPrincipal() * (1 + p.getAnnualReturnRate().doubleValue() / 100.0));
    }
}
