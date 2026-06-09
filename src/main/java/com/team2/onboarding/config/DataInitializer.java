package com.team2.onboarding.config;

import com.team2.onboarding.entity.*;
import com.team2.onboarding.enums.EmployeeType;
import com.team2.onboarding.enums.PaymentCycle;
import com.team2.onboarding.enums.PlanType;
import com.team2.onboarding.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ── 공통 / DC 리포지토리 ──────────────────────────────────────
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final AnnualSalaryRepository annualSalaryRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final CompanyRetirementDcRepository companyRetirementDcRepository;
    private final EmployeeRetirementDcRepository employeeRetirementDcRepository;
    private final ContributionRepository contributionRepository;

    // ── DB 리포지토리 ─────────────────────────────────────────────
    private final CompanyRetirementDbRepository companyRetirementDbRepository;
    private final EmployeeRetirementDbRepository employeeRetirementDbRepository;
    private final AnnualSalaryDbRepository annualSalaryDbRepository;
    private final FeePaymentDbRepository feePaymentDbRepository;
    private final ReserveDbRepository reserveDbRepository;
    private final InvestmentProductMasterRepository investmentProductMasterRepository;  // 마스터
    private final InvestmentProductDbRepository investmentProductDbRepository;    // 매수 상품

    @Bean
    @Transactional
    CommandLineRunner initData() {
        return args -> {
            if (companyRepository.count() > 0) return;

            int year = LocalDate.now().getYear();

            // ── 0. 운용상품 마스터 (전역 데이터, 회사 루프 전에 1번만) ──
            List<InvestmentProductMaster> productMasters = createProductMasters();

            // 7개 회사: DC×5 + DB×2
            String[][] companyData = {
                    {"1008100001", "삼성전자",   "이재용"},
                    {"1008100002", "현대자동차", "정의선"},
                    {"1008100003", "카카오",     "홍은택"},
                    {"1008100004", "네이버",     "최수연"},
                    {"1008100005", "LG전자",     "조주완"},
                    {"1008100006", "포스코",     "최정우"},      // DB형 - 추가납입필요
                    {"1008100007", "SK하이닉스", "곽노정"},     // DB형 - 적정
            };
            PlanType[] planTypes = {
                    PlanType.DC, PlanType.DC, PlanType.DC, PlanType.DC, PlanType.DC,
                    PlanType.DB, PlanType.DB,
            };
            PaymentCycle[] cycles = {
                    PaymentCycle.MONTHLY,
                    PaymentCycle.QUARTERLY,
                    PaymentCycle.YEARLY,
                    PaymentCycle.MONTHLY,
                    PaymentCycle.QUARTERLY,
                    null, null,  // DB형은 사용 안 함
            };
            LocalDate[] contractDates = {
                    LocalDate.of(2021, 1,  1),
                    LocalDate.of(2021, 4,  1),
                    LocalDate.of(2020, 7,  1),
                    LocalDate.of(2022, 1,  1),
                    LocalDate.of(2021, 10, 1),
                    LocalDate.of(2020, 3,  1),
                    LocalDate.of(2019, 6,  1),
            };

            String[][] employeeData = {
                    {"이재원", "900101"},
                    {"김민준", "850615"},
                    {"박지수", "920303"},
                    {"이수진", "780920"},
                    {"최민혁", "801212"},
                    {"정유진", "950430"},
                    {"한지민", "881205"},
                    {"오세훈", "760312"},
                    {"강다은", "930820"},
                    {"윤서준", "971115"},
            };

            // ── 회사별 default_option Y/N 비중 (DC 전용) ──
            //   직원 10명 중 앞에서부터 Y, 나머지 N → 회사마다 Y 개수 차등
            //   ci=0: Y2/N8, ci=1: Y4/N6, ci=2: Y6/N4, ci=3: Y8/N2, ci=4: Y3/N7
            String[][] defaultOptionsByCompany = {
                    {"Y", "Y", "N", "N", "N", "N", "N", "N", "N", "N"}, // 삼성전자  Y2
                    {"Y", "Y", "Y", "Y", "N", "N", "N", "N", "N", "N"}, // 현대자동차 Y4
                    {"Y", "Y", "Y", "Y", "Y", "Y", "N", "N", "N", "N"}, // 카카오    Y6
                    {"Y", "Y", "Y", "Y", "Y", "Y", "Y", "Y", "N", "N"}, // 네이버    Y8
                    {"Y", "Y", "Y", "N", "N", "N", "N", "N", "N", "N"}, // LG전자    Y3
            };

            // ── 회사별 has_irp_account Y/N 비중 (DC/DB 공통) ──
            //   ei 8,9는 퇴사자라 항상 Y로 덮어씀 → 아래는 재직자(0~7) 기준 패턴
            //   ci=0: 재직Y1, ci=1: Y3, ci=2: Y2, ci=3: Y5, ci=4: Y4 (DC)
            //   ci=5: Y6, ci=6: Y2 (DB)
            String[][] hasIrpByCompany = {
                    {"Y", "N", "N", "N", "N", "N", "N", "N", "Y", "Y"}, // 삼성전자  재직Y1
                    {"Y", "Y", "Y", "N", "N", "N", "N", "N", "Y", "Y"}, // 현대자동차 재직Y3
                    {"Y", "Y", "N", "N", "N", "N", "N", "N", "Y", "Y"}, // 카카오    재직Y2
                    {"Y", "Y", "Y", "Y", "Y", "N", "N", "N", "Y", "Y"}, // 네이버    재직Y5
                    {"Y", "Y", "Y", "Y", "N", "N", "N", "N", "Y", "Y"}, // LG전자    재직Y4
                    {"Y", "Y", "Y", "Y", "Y", "Y", "N", "N", "Y", "Y"}, // 포스코    재직Y6
                    {"Y", "Y", "N", "N", "N", "N", "N", "N", "Y", "Y"}, // SK하이닉스 재직Y2
            };

            for (int ci = 0; ci < companyData.length; ci++) {

                Company company = companyRepository.save(Company.builder()
                        .brn(companyData[ci][0])
                        .companyName(companyData[ci][1])
                        .representativeName(companyData[ci][2])
                        .password(passwordEncoder.encode("1234"))
                        .planType(planTypes[ci])
                        .build());

                if (planTypes[ci] == PlanType.DC) {
                    insertDcData(ci, company, contractDates[ci], cycles[ci], year,
                            employeeData, defaultOptionsByCompany[ci], hasIrpByCompany[ci]);
                } else {
                    insertDbData(ci, company, contractDates[ci], year,
                            employeeData, productMasters, hasIrpByCompany[ci]);
                }
            }

            System.out.println("=== Mock Data Insert Complete ===");
        };
    }

    // ═════════════════════════════════════════════════════════════
    // DC형 데이터 (기존 로직 유지)
    // ═════════════════════════════════════════════════════════════
    private void insertDcData(int ci, Company company, LocalDate contractDate,
                              PaymentCycle cycle, int year,
                              String[][] employeeData,
                              String[] defaultOptions, String[] hasIrpOptions) {

        CompanyRetirementDc crd = companyRetirementDcRepository.save(CompanyRetirementDc.builder()
                .companyAccount(String.format("ACC-DC-%03d", ci + 1))
                .planType(PlanType.DC)
                .contractDate(contractDate)
                .paymentCycle(cycle)
                .company(company)
                .build());

        // 부담금
        long baseContribution = 80_000_000L + (long) ci * 20_000_000L;
        insertContributions(year, cycle, baseContribution, crd);

        // 수수료
        for (String feeType : new String[]{"운용관리", "자산관리"}) {
            long feeAmount = "운용관리".equals(feeType)
                    ? 400_000L + (long) ci * 50_000L
                    : 250_000L + (long) ci * 30_000L;
            for (int y = year - 2; y <= year; y++) {
                boolean paid = y < year;
                LocalDate feeDue = contractDate.withYear(y);
                feePaymentRepository.save(FeePayment.builder()
                        .paymentYear(y)
                        .dueDate(feeDue)
                        .feeAmount(feeAmount)
                        .feeType(feeType)
                        .paidDate(paid ? feeDue.plusDays(5) : null)
                        .status(paid ? "납입완료" : "미납")
                        .companyRetirementDc(crd)
                        .build());
            }
        }

        // 직원 10명
        for (int ei = 0; ei < employeeData.length; ei++) {
            int globalIdx = ci * 10 + ei;
            Employee employee = saveEmployee(ci, ei, globalIdx, company, employeeData, year);
            boolean isTerminated = ei >= 8;

            employeeRetirementDcRepository.save(EmployeeRetirementDc.builder()
                    .employeeAccount(String.format("DC-%04d", globalIdx + 1))
                    .accountType("DC")
                    .joinDate(LocalDate.of(2018 + ci, 4, 1))
                    .effectiveDate(LocalDate.of(2018 + ci, 3, 1))
                    .defaultOption(defaultOptions[ei])
                    .hasIrpAccount(hasIrpOptions[ei])
                    .employee(employee)
                    .companyRetirementDc(crd)
                    .build());

            // 연간임금 (DC: salary + min_contribution + contribution)
            long baseSalary = baseSalaryByType(employee.getEmployeeType(), ei);
            for (int y = year - 1; y <= year; y++) {
                long salary = baseSalary + (long) (y - year + 1) * 3_000_000L;
                long minContribution = salary / 12;
                long contribution = minContribution + 200_000L * (ei + 1);

                annualSalaryRepository.save(AnnualSalary.builder()
                        .year(String.valueOf(y))
                        .salary(salary)
                        .minContribution(minContribution)
                        .contribution(contribution)
                        .employee(employee)
                        .build());
            }
        }
    }

    // ═════════════════════════════════════════════════════════════
    // DB형 데이터
    // ═════════════════════════════════════════════════════════════
    private void insertDbData(int ci, Company company, LocalDate contractDate, int year,
                              String[][] employeeData,
                              List<InvestmentProductMaster> productMasters,
                              String[] hasIrpOptions) {

        CompanyRetirementDb crdb = companyRetirementDbRepository.save(CompanyRetirementDb.builder()
                .companyAccount(String.format("ACC-DB-%03d", ci + 1))
                .planType(PlanType.DB)
                .contractDate(contractDate)
                .fiscalMonth(12)
                .company(company)
                .build());

        // 수수료 납입 이력 (DB)
        for (String feeType : new String[]{"운용관리", "자산관리"}) {
            long feeAmount = "운용관리".equals(feeType)
                    ? 400_000L + (long) ci * 50_000L
                    : 250_000L + (long) ci * 30_000L;
            for (int y = year - 2; y <= year; y++) {
                boolean paid = y < year;
                LocalDate feeDue = contractDate.withYear(y);
                feePaymentDbRepository.save(FeePaymentDb.builder()
                        .paymentYear(y)
                        .dueDate(feeDue)
                        .feeAmount(feeAmount)
                        .feeType(feeType)
                        .paidDate(paid ? feeDue.plusDays(5) : null)
                        .status(paid ? "납입완료" : "미납")
                        .companyRetirementDb(crdb)
                        .build());
            }
        }

        // 직원 10명 + 추계액 합산
        LocalDate baseDate = LocalDate.of(year - 1, 12, 31);  // 직전 결산일
        long totalBenefitObligation = 0L;

        for (int ei = 0; ei < employeeData.length; ei++) {
            int globalIdx = ci * 10 + ei;
            Employee employee = saveEmployee(ci, ei, globalIdx, company, employeeData, year);

            employeeRetirementDbRepository.save(EmployeeRetirementDb.builder()
                    .accountType("DB")
                    .employeeAccount(String.format("DB-%04d", globalIdx + 1))
                    .joinDate(LocalDate.of(2018 + ci, 4, 1))
                    .hasIrpAccount(hasIrpOptions[ei])
                    .employee(employee)
                    .companyRetirementDb(crdb)
                    .build());

            // 연간임금 (DB: salary만)
            long baseSalary = baseSalaryByType(employee.getEmployeeType(), ei);
            long currentYearSalary = 0L;
            for (int y = year - 1; y <= year; y++) {
                long salary = baseSalary + (long) (y - year + 1) * 3_000_000L;
                if (y == year - 1) currentYearSalary = salary;  // 결산 기준 연봉

                annualSalaryDbRepository.save(AnnualSalaryDb.builder()
                        .year(String.valueOf(y))
                        .salary(salary)
                        .employee(employee)
                        .build());
            }

            // 추계액 계산: salary × 30/365 × 재직일수/365
            long serviceDays = ChronoUnit.DAYS.between(employee.getStartDate(), baseDate);
            if (serviceDays > 0) {
                long estimatedBenefit = Math.round(
                        currentYearSalary * 30.0 / 365.0 * serviceDays / 365.0
                );
                totalBenefitObligation += estimatedBenefit;
            }
        }

        // 운용상품 매수 (회사별 패턴)
        long fundedAmount = insertInvestmentProducts(ci, crdb, productMasters);

        // 재정검증 (reserve_db)
        insertReserve(crdb, baseDate, totalBenefitObligation, fundedAmount);
    }

    // ─────────────────────────────────────────────────────────────
    // 공통 유틸
    // ─────────────────────────────────────────────────────────────
    private Employee saveEmployee(int ci, int ei, int globalIdx, Company company,
                                  String[][] employeeData, int year) {
        String rrn = employeeData[ei][1] + String.format("%07d", globalIdx + 1);
        boolean isTerminated = ei >= 8;
        LocalDate terminationDate = !isTerminated ? null
                : (ei == 8 ? LocalDate.of(year - 1, 8, 31) : LocalDate.of(year - 1, 7, 15));
        EmployeeType empType = ei < 2 ? EmployeeType.EXECUTIVE : EmployeeType.EMPLOYEE;

        return employeeRepository.save(Employee.builder()
                .name(employeeData[ei][0])
                .rrn(rrn)
                .employeeType(empType)
                .startDate(LocalDate.of(2018 + ci, 3, 1))
                .terminationDate(terminationDate)
                .company(company)
                .build());
    }

    private long baseSalaryByType(EmployeeType empType, int ei) {
        return empType == EmployeeType.EXECUTIVE
                ? 100_000_000L + (long) (ei % 2) * 20_000_000L
                : 40_000_000L + (long) (ei - 2) * 5_000_000L;
    }

    // ─────────────────────────────────────────────────────────────
    // 운용상품 마스터 (전역)
    // ─────────────────────────────────────────────────────────────
    private List<InvestmentProductMaster> createProductMasters() {
        Object[][] data = {
                // {productProvider, category, isPrincipalGuaranteed, annualReturnRate}
                {"IBK기업은행",   "정기예금",       true,  "3.50"},
                {"KB국민은행",    "정기예금",       true,  "3.20"},
                {"신한은행",      "정기예금",       true,  "3.00"},
                {"삼성생명",      "이율보증형보험",  true,  "2.80"},
                {"한화생명",      "이율보증형보험",  true,  "2.50"},
                {"미래에셋증권",  "ELB 및 ELD",    true,  "4.20"},
                {"NH투자증권",    "ELB 및 ELD",    true,  "4.50"},
                {"KB증권",        "ELB 및 ELD",    true,  "3.80"},
                {"한국투자증권",  "ELB 및 ELD",    true,  "4.00"},
        };
        List<InvestmentProductMaster> result = new ArrayList<>();
        for (Object[] row : data) {
            result.add(investmentProductMasterRepository.save(InvestmentProductMaster.builder()
                    .productProvider((String) row[0])
                    .productCategory((String) row[1])
                    .isPrincipalGuaranteed((Boolean) row[2])
                    .annualReturnRate(new BigDecimal((String) row[3]))
                    .build()));
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────
    // 회사별 운용상품 매수
    //   ci=5 (포스코)     → 부족 상태 (추가납입필요)
    //   ci=6 (SK하이닉스) → 적정 상태
    // ─────────────────────────────────────────────────────────────
    private long insertInvestmentProducts(int ci, CompanyRetirementDb crdb,
                                          List<InvestmentProductMaster> masters) {
        // {masterIdx, principal, maturityMonthsOffset, status}
        Object[][] portfolio = (ci == 5)
                ? new Object[][]{
                {0, 800_000_000L,    1, "운용중"},     // IBK A 8억, 만기 임박
                {1, 700_000_000L,    2, "운용중"},     // IBK B 7억
                {3, 900_000_000L,    6, "운용중"},     // 채권형 A 9억
                {5, 600_000_000L,   12, "운용중"},     // 혼합형 A 6억
                {7, 400_000_000L,    3, "운용중"},     // MMF 4억
                {2, 600_000_000L,  -12, "만기완료"},   // KB C 6억, 작년 만기 (확정금액)
        }
                : new Object[][]{
                {0, 1_500_000_000L,  4, "운용중"},
                {1, 1_200_000_000L,  8, "운용중"},
                {3, 1_000_000_000L,  6, "운용중"},
                {4,   800_000_000L, 10, "운용중"},
                {5,   700_000_000L, 12, "운용중"},
                {7,   500_000_000L,  2, "운용중"},
        };

        long totalFunded = 0L;
        for (Object[] item : portfolio) {
            int masterIdx = (int) item[0];
            long principal = (long) item[1];
            int monthsOffset = (int) item[2];
            String status = (String) item[3];

            InvestmentProductMaster master = masters.get(masterIdx);
            LocalDate maturityDate = LocalDate.now().plusMonths(monthsOffset);
            BigDecimal rate = master.getAnnualReturnRate();

            // 평가금액 = 원금 × (1 + 수익률)
            long evaluatedAmount = Math.round(
                    principal * (1 + rate.doubleValue() / 100.0)
            );

            Long confirmedAmount = "만기완료".equals(status) ? evaluatedAmount : null;
            totalFunded += evaluatedAmount;

            investmentProductDbRepository.save(InvestmentProductDb.builder()
                    .principal(principal)
                    .annualReturnRate(rate)
                    .maturityDate(maturityDate)
                    .confirmedAmount(confirmedAmount)
                    .status(status)
                    .productMaster(master)
                    .companyRetirementDb(crdb)
                    .build());
        }
        return totalFunded;
    }

    // ─────────────────────────────────────────────────────────────
    // 재정검증 결과 (reserve_db) INSERT
    // ─────────────────────────────────────────────────────────────
    private void insertReserve(CompanyRetirementDb crdb, LocalDate baseDate,
                               long benefitObligation, long fundedAmount) {
        long minReserve = benefitObligation;  // 법정 100% 기준
        long shortfall = Math.max(0, minReserve - fundedAmount);

        BigDecimal fundingRatio = BigDecimal.valueOf(fundedAmount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(minReserve), 2, RoundingMode.HALF_UP);

        String status;
        if (fundingRatio.compareTo(BigDecimal.valueOf(100)) >= 0)      status = "적정";
        else if (fundingRatio.compareTo(BigDecimal.valueOf(95)) >= 0)  status = "주의";
        else                                                            status = "추가납입필요";

        reserveDbRepository.save(ReserveDb.builder()
                .baseDate(baseDate)
                .benefitObligation(benefitObligation)
                .minReserve(minReserve)
                .fundedAmount(fundedAmount)
                .fundingRatio(fundingRatio)
                .shortfallAmount(shortfall)
                .additionalDueDate(baseDate.plusYears(1))
                .status(status)
                .companyRetirementDb(crdb)
                .build());
    }

    // ─────────────────────────────────────────────────────────────
    // DC 부담금 (기존 로직 유지)
    // ─────────────────────────────────────────────────────────────
    private void insertContributions(int year, PaymentCycle cycle, long baseAmount, CompanyRetirementDc crd) {
        switch (cycle) {
            case MONTHLY -> {
                for (int month = 1; month <= 12; month++) {
                    LocalDate dueDate = LocalDate.of(year, month, 25);
                    String status = month < 6 ? "납입완료" : month == 6 ? "미납" : "예정";
                    contributionRepository.save(Contribution.builder()
                            .contributionAmount(baseAmount)
                            .dueDate(dueDate)
                            .paidDate(month < 6 ? dueDate.minusDays(2) : null)
                            .status(status)
                            .cycle(PaymentCycle.MONTHLY)
                            .companyRetirementDc(crd)
                            .build());
                }
            }
            case QUARTERLY -> {
                int[][] quarters = {{3, 31}, {6, 30}, {9, 30}, {12, 31}};
                String[] statuses = {"납입완료", "미납", "예정", "예정"};
                for (int q = 0; q < 4; q++) {
                    LocalDate dueDate = LocalDate.of(year, quarters[q][0], quarters[q][1]);
                    contributionRepository.save(Contribution.builder()
                            .contributionAmount(baseAmount * 3)
                            .dueDate(dueDate)
                            .paidDate("납입완료".equals(statuses[q]) ? dueDate.minusDays(3) : null)
                            .status(statuses[q])
                            .cycle(PaymentCycle.QUARTERLY)
                            .companyRetirementDc(crd)
                            .build());
                }
            }
            case YEARLY -> {
                String[] statuses = {"납입완료", "미납", "예정"};
                for (int i = 0; i < 3; i++) {
                    int y = year - 2 + i;
                    LocalDate dueDate = LocalDate.of(y, 12, 31);
                    contributionRepository.save(Contribution.builder()
                            .contributionAmount(baseAmount * 12)
                            .dueDate(dueDate)
                            .paidDate("납입완료".equals(statuses[i]) ? dueDate.minusDays(10) : null)
                            .status(statuses[i])
                            .cycle(PaymentCycle.YEARLY)
                            .companyRetirementDc(crd)
                            .build());
                }
            }
        }
    }
}