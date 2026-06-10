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
import java.util.Random;

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
    private final InvestmentProductMasterRepository investmentProductMasterRepository;
    private final InvestmentProductDbRepository investmentProductDbRepository;

    // ── 직원 이름/생년 풀 (40명) ──────────────────────────────────
    private static final String[][] EMPLOYEE_POOL = {
            {"이재원", "900101"}, {"김민준", "850615"}, {"박지수", "920303"},
            {"이수진", "780920"}, {"최민혁", "801212"}, {"정유진", "950430"},
            {"한지민", "881205"}, {"오세훈", "760312"}, {"강다은", "930820"},
            {"윤서준", "971115"}, {"서하준", "880407"}, {"조은혜", "910225"},
            {"임도현", "860918"}, {"신지원", "940612"}, {"류성민", "830503"},
            {"고은서", "960817"}, {"배준혁", "790124"}, {"전수빈", "870930"},
            {"황민서", "920505"}, {"문지훈", "850716"}, {"장서연", "990214"},
            {"남기현", "810828"}, {"노유진", "930611"}, {"심재호", "880302"},
            {"안소희", "910925"}, {"권태양", "820414"}, {"허나연", "950708"},
            {"방준서", "871020"}, {"유지호", "960319"}, {"석민아", "840607"},
            {"탁지현", "920113"}, {"봉우현", "870522"}, {"원서진", "940830"},
            {"도현우", "800215"}, {"차혜리", "910704"}, {"모지수", "850311"},
            {"소재현", "970615"}, {"표민준", "830928"}, {"지예진", "900817"},
            {"견서준", "860203"},
    };

    @Bean
    @Transactional
    CommandLineRunner initData() {
        return args -> {
            if (companyRepository.count() > 0) return;

            int year = LocalDate.now().getYear();
            List<InvestmentProductMaster> productMasters = createProductMasters();

            // ═══════════════════════════════════════════════════════
            // DC 회사 (5개) - 기존 로직 유지
            // ═══════════════════════════════════════════════════════
            String[][] dcCompanyData = {
                    {"1008100001", "삼성전자",   "이재용"},
                    {"1008100002", "현대자동차", "정의선"},
                    {"1008100003", "카카오",     "홍은택"},
                    {"1008100004", "네이버",     "최수연"},
                    {"1008100005", "LG전자",     "조주완"},
            };
            PaymentCycle[] dcCycles = {
                    PaymentCycle.MONTHLY, PaymentCycle.QUARTERLY, PaymentCycle.YEARLY,
                    PaymentCycle.MONTHLY, PaymentCycle.QUARTERLY,
            };
            LocalDate[] dcContractDates = {
                    LocalDate.of(2021, 1,  1),
                    LocalDate.of(2021, 4,  1),
                    LocalDate.of(2020, 7,  1),
                    LocalDate.of(2022, 1,  1),
                    LocalDate.of(2021, 10, 1),
            };

            String[][] dcEmployeeData = {
                    {"이재원", "900101"}, {"김민준", "850615"}, {"박지수", "920303"},
                    {"이수진", "780920"}, {"최민혁", "801212"}, {"정유진", "950430"},
                    {"한지민", "881205"}, {"오세훈", "760312"}, {"강다은", "930820"},
                    {"윤서준", "971115"},
            };
            String[][] defaultOptionsByCompany = {
                    {"Y", "Y", "N", "N", "N", "N", "N", "N", "N", "N"},
                    {"Y", "Y", "Y", "Y", "N", "N", "N", "N", "N", "N"},
                    {"Y", "Y", "Y", "Y", "Y", "Y", "N", "N", "N", "N"},
                    {"Y", "Y", "Y", "Y", "Y", "Y", "Y", "Y", "N", "N"},
                    {"Y", "Y", "Y", "N", "N", "N", "N", "N", "N", "N"},
            };
            String[][] dcHasIrpByCompany = {
                    {"Y", "N", "N", "N", "N", "N", "N", "N", "Y", "Y"},
                    {"Y", "Y", "Y", "N", "N", "N", "N", "N", "Y", "Y"},
                    {"Y", "Y", "N", "N", "N", "N", "N", "N", "Y", "Y"},
                    {"Y", "Y", "Y", "Y", "Y", "N", "N", "N", "Y", "Y"},
                    {"Y", "Y", "Y", "Y", "N", "N", "N", "N", "Y", "Y"},
            };

            for (int ci = 0; ci < dcCompanyData.length; ci++) {
                Company company = companyRepository.save(Company.builder()
                        .brn(dcCompanyData[ci][0])
                        .companyName(dcCompanyData[ci][1])
                        .representativeName(dcCompanyData[ci][2])
                        .password(passwordEncoder.encode("1234"))
                        .planType(PlanType.DC)
                        .build());

                insertDcData(ci, company, dcContractDates[ci], dcCycles[ci], year,
                        dcEmployeeData, defaultOptionsByCompany[ci], dcHasIrpByCompany[ci]);
            }

            // ═══════════════════════════════════════════════════════
            // DB 회사 (8개)
            // {brn, name, rep, contractYear, M, D, empCount, targetRatio, irpRatio, portfolioType, feeBase만원}
            // targetRatio → 재정 상태:
            //   ≥100% : 적정   (SK하이닉스 102, 한화에어로스페이스 106, GS칼텍스 101)
            //   95~99%: 주의   (현대제철 96, 두산에너빌리티 97)
            //   <95%  : 추가납입필요 (포스코 83, 롯데케미칼 88, OCI 91)
            // ═══════════════════════════════════════════════════════
            Object[][] dbConfigs = {
                //  brn            name              rep       cY    cM cD  emp  ratio  irp  port  fee
                {"1008100006", "포스코",             "최정우", 2020,  3, 1,  25,  83.0, 0.60,  3,  40},
                {"1008100007", "SK하이닉스",         "곽노정", 2019,  6, 1,  28, 102.0, 0.40,  1,  55},
                {"1008100008", "현대제철",           "안동일", 2018,  9, 1,  22,  96.0, 0.50,  0,  45},
                {"1008100009", "롯데케미칼",         "김교현", 2021,  2, 1,  24,  88.0, 0.30,  2,  38},
                {"1008100010", "한화에어로스페이스", "손재일", 2017,  5, 1,  30, 106.0, 0.70,  2,  70},
                {"1008100011", "두산에너빌리티",     "정연인", 2022,  1, 1,  20,  97.0, 0.50,  0,  32},
                {"1008100012", "GS칼텍스",           "허세홍", 2019,  3, 1,  26, 101.0, 0.40,  1,  50},
                {"1008100013", "OCI",               "이우현", 2020,  7, 1,  23,  91.0, 0.30,  3,  35},
            };

            // DC 5개 × 10명 = 50부터 DB 직원 전역 인덱스 시작
            int dbGlobalIdx = 50;

            for (int dbi = 0; dbi < dbConfigs.length; dbi++) {
                Object[] cfg = dbConfigs[dbi];
                LocalDate contractDate = LocalDate.of((int) cfg[3], (int) cfg[4], (int) cfg[5]);

                Company company = companyRepository.save(Company.builder()
                        .brn((String) cfg[0])
                        .companyName((String) cfg[1])
                        .representativeName((String) cfg[2])
                        .password(passwordEncoder.encode("1234"))
                        .planType(PlanType.DB)
                        .build());

                insertDbData(
                        dbi, company, contractDate, year, productMasters,
                        (int)    cfg[6],   // empCount
                        (double) cfg[7],   // targetFundingRatio
                        (double) cfg[8],   // irpRatio
                        (int)    cfg[9],   // portfolioType
                        (int)    cfg[10],  // feeBase (만원 단위)
                        dbGlobalIdx
                );
                dbGlobalIdx += (int) cfg[6];
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

        // 직원·연봉 먼저 생성하고 당해 contribution 합산
        long totalContribution = 0L;
        for (int ei = 0; ei < employeeData.length; ei++) {
            int globalIdx = ci * 10 + ei;
            Employee employee = saveDcEmployee(ci, ei, globalIdx, company, employeeData, year);

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

                if (y == year) {
                    totalContribution += contribution;
                }
            }
        }

        // 연간 총 contribution 기준으로 납입 스케줄 생성
        insertContributions(year, cycle, totalContribution, crd);
    }

    // ═════════════════════════════════════════════════════════════
    // DB형 데이터
    // ═════════════════════════════════════════════════════════════
    private void insertDbData(int dbi, Company company, LocalDate contractDate, int year,
                              List<InvestmentProductMaster> productMasters,
                              int empCount, double targetFundingRatio,
                              double irpRatio, int portfolioType,
                              int feeBaseMan, int globalIdxStart) {

        CompanyRetirementDb crdb = companyRetirementDbRepository.save(CompanyRetirementDb.builder()
                .companyAccount(String.format("ACC-DB-%03d", dbi + 1))
                .planType(PlanType.DB)
                .contractDate(contractDate)
                .fiscalMonth(12)
                .company(company)
                .build());

        // 수수료 납입 이력
        for (String feeType : new String[]{"운용관리", "자산관리"}) {
            long feeAmount = "운용관리".equals(feeType)
                    ? feeBaseMan * 10_000L
                    : (long) Math.round(feeBaseMan * 10_000L * 0.6);
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

        // 근속 패턴: ei % 5 별로 입사일 결정 (다양한 재직기간)
        int[] tenureYears = {12, 8, 5, 3, 1};
        LocalDate baseDate = LocalDate.of(year - 1, 12, 31);  // 직전 결산일
        long totalBenefitObligation = 0L;
        int irpThreshold = (int) Math.round(empCount * irpRatio);
        int terminatedStart = empCount - 2;  // 마지막 2명 퇴사자

        for (int ei = 0; ei < empCount; ei++) {
            int globalIdx = globalIdxStart + ei;
            String[] emp = EMPLOYEE_POOL[globalIdx % EMPLOYEE_POOL.length];

            // 근속 기간에 따라 입사일 다양화
            int tenure = tenureYears[ei % 5];
            LocalDate startDate = contractDate.minusYears(tenure).withDayOfMonth(1);

            boolean isTerminated = ei >= terminatedStart;
            LocalDate terminationDate = isTerminated
                    ? (ei == terminatedStart
                            ? LocalDate.of(year - 1, 9, 30)
                            : LocalDate.of(year - 1, 8, 15))
                    : null;

            EmployeeType empType = ei < 2 ? EmployeeType.EXECUTIVE : EmployeeType.EMPLOYEE;

            Employee employee = employeeRepository.save(Employee.builder()
                    .name(emp[0])
                    .rrn(emp[1] + String.format("%07d", globalIdx + 1))
                    .employeeType(empType)
                    .startDate(startDate)
                    .terminationDate(terminationDate)
                    .company(company)
                    .build());

            String hasIrp = (ei < irpThreshold || isTerminated) ? "Y" : "N";

            employeeRetirementDbRepository.save(EmployeeRetirementDb.builder()
                    .accountType("DB")
                    .employeeAccount(String.format("DB-%04d", globalIdx + 1))
                    .joinDate(startDate)
                    .hasIrpAccount(hasIrp)
                    .employee(employee)
                    .companyRetirementDb(crdb)
                    .build());

            // 연봉: 직책 + 근속 가산
            long baseSalary = empType == EmployeeType.EXECUTIVE
                    ? 90_000_000L + (long) tenure * 2_000_000L
                    : 35_000_000L + (long) tenure * 1_500_000L;

            long currentYearSalary = 0L;
            for (int y = year - 1; y <= year; y++) {
                long salary = baseSalary + (long) (y - (year - 1)) * 3_000_000L;
                if (y == year - 1) currentYearSalary = salary;

                annualSalaryDbRepository.save(AnnualSalaryDb.builder()
                        .year(String.valueOf(y))
                        .salary(salary)
                        .employee(employee)
                        .build());
            }

            // 추계액 = 평균임금(30일분) × 재직일수/365
            long serviceDays = ChronoUnit.DAYS.between(startDate, baseDate);
            if (serviceDays > 0) {
                long estimatedBenefit = Math.round(
                        currentYearSalary * 30.0 / 365.0 * serviceDays / 365.0
                );
                totalBenefitObligation += estimatedBenefit;
            }
        }

        long fundedAmount = insertInvestmentProducts(
                portfolioType, crdb, productMasters, totalBenefitObligation, targetFundingRatio);
        insertReserve(crdb, baseDate, totalBenefitObligation, fundedAmount);
    }

    // ─────────────────────────────────────────────────────────────
    // 운용상품 매수 — 포트폴리오 유형별 상품 구성 + targetFundingRatio 스케일링
    // ─────────────────────────────────────────────────────────────
    private long insertInvestmentProducts(int portfolioType, CompanyRetirementDb crdb,
                                          List<InvestmentProductMaster> masters,
                                          long totalBenefitObligation,
                                          double targetFundingRatio) {
        // {masterIdx, 비율(%), maturityMonthsOffset, status}
        Object[][] portfolio = switch (portfolioType) {
            case 0 -> new Object[][]{ // 보수형 — 예금·보험 위주
                    {0, 35.0,  6, "운용중"},  // IBK 정기예금
                    {1, 30.0, 10, "운용중"},  // KB 정기예금
                    {2, 25.0,  8, "운용중"},  // 신한은행 정기예금
                    {3, 10.0, 14, "운용중"},  // 삼성생명 이율보증형
            };
            case 1 -> new Object[][]{ // 균형형 — 예금 + ELB 혼합
                    {0, 25.0,  4, "운용중"},  // IBK 정기예금
                    {1, 20.0,  8, "운용중"},  // KB 정기예금
                    {3, 15.0, 12, "운용중"},  // 삼성생명
                    {4, 10.0, 16, "운용중"},  // 한화생명
                    {5, 18.0,  6, "운용중"},  // 미래에셋 ELB
                    {6, 12.0,  3, "운용중"},  // NH투자 ELB
            };
            case 2 -> new Object[][]{ // 성장형 — ELB 위주
                    {0, 20.0,  4, "운용중"},  // IBK 정기예금
                    {2, 10.0,  8, "운용중"},  // 신한은행
                    {5, 25.0,  6, "운용중"},  // 미래에셋 ELB
                    {6, 25.0,  9, "운용중"},  // NH투자 ELB
                    {7, 20.0,  5, "운용중"},  // KB증권 ELB
            };
            default -> new Object[][]{ // 혼합형 — 만기완료 포함
                    {0, 20.0,  1, "운용중"},   // IBK 정기예금, 만기 임박
                    {1, 17.5,  2, "운용중"},   // KB 정기예금
                    {3, 22.5,  6, "운용중"},   // 삼성생명
                    {5, 15.0, 12, "운용중"},   // 미래에셋 ELB
                    {7, 10.0,  3, "운용중"},   // KB증권 ELB
                    {2, 15.0, -12, "만기완료"}, // 신한은행, 작년 만기
            };
        };

        // 비율 합계 = 100%일 때 예상 funded/benefitObligation 비율 계산
        double normalizedFundedRatio = 0.0;
        for (Object[] item : portfolio) {
            int masterIdx = (int) item[0];
            double ratio = (double) item[1];
            double rate = masters.get(masterIdx).getAnnualReturnRate().doubleValue();
            normalizedFundedRatio += ratio / 100.0 * (1.0 + rate / 100.0);
        }
        // scalingFactor: 원금 총합을 조정해 funded_amount ≈ benefitObligation × targetRatio%
        double scalingFactor = (targetFundingRatio / 100.0) / normalizedFundedRatio;

        long totalFunded = 0L;
        for (Object[] item : portfolio) {
            int masterIdx  = (int)    item[0];
            double ratio   = (double) item[1];
            int monthsOffset = (int)  item[2];
            String status  = (String) item[3];

            long principal = Math.round(totalBenefitObligation * ratio / 100.0 * scalingFactor);
            InvestmentProductMaster master = masters.get(masterIdx);
            BigDecimal rate = master.getAnnualReturnRate();

            long evaluatedAmount = Math.round(principal * (1 + rate.doubleValue() / 100.0));
            Long confirmedAmount = "만기완료".equals(status) ? evaluatedAmount : null;
            totalFunded += evaluatedAmount;

            investmentProductDbRepository.save(InvestmentProductDb.builder()
                    .principal(principal)
                    .annualReturnRate(rate)
                    .maturityDate(LocalDate.now().plusMonths(monthsOffset))
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
        long minReserve = benefitObligation;
        long shortfall = Math.max(0, minReserve - fundedAmount);

        BigDecimal fundingRatio = BigDecimal.valueOf(fundedAmount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(minReserve), 2, RoundingMode.HALF_UP);

        String status;
        if (fundingRatio.compareTo(BigDecimal.valueOf(100)) >= 0)     status = "적정";
        else if (fundingRatio.compareTo(BigDecimal.valueOf(95)) >= 0) status = "주의";
        else                                                           status = "추가납입필요";

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
    // 운용상품 마스터 (전역)
    // ─────────────────────────────────────────────────────────────
    private List<InvestmentProductMaster> createProductMasters() {
        Object[][] data = {
                // {productProvider, category, isPrincipalGuaranteed, annualReturnRate}
                {"IBK기업은행",  "정기예금",        true, "3.50"},  // idx 0
                {"KB국민은행",   "정기예금",        true, "3.20"},  // idx 1
                {"신한은행",     "정기예금",        true, "3.00"},  // idx 2
                {"삼성생명",     "이율보증형보험",   true, "2.80"},  // idx 3
                {"한화생명",     "이율보증형보험",   true, "2.50"},  // idx 4
                {"미래에셋증권", "ELB 및 ELD",      true, "4.20"},  // idx 5
                {"NH투자증권",   "ELB 및 ELD",      true, "4.50"},  // idx 6
                {"KB증권",       "ELB 및 ELD",      true, "3.80"},  // idx 7
                {"한국투자증권", "ELB 및 ELD",      true, "4.00"},  // idx 8
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
    // 공통 유틸
    // ─────────────────────────────────────────────────────────────
    private Employee saveDcEmployee(int ci, int ei, int globalIdx, Company company,
                                    String[][] employeeData, int year) {
        String rrn = employeeData[ei][1] + String.format("%07d", globalIdx + 1);
        boolean isTerminated = ei >= 8;
        LocalDate terminationDate = !isTerminated ? null
                : (ei == 8 ? LocalDate.of(year - 1, 8, 31) : LocalDate.of(year - 1, 7, 15));

        return employeeRepository.save(Employee.builder()
                .name(employeeData[ei][0])
                .rrn(rrn)
                .employeeType(ei < 2 ? EmployeeType.EXECUTIVE : EmployeeType.EMPLOYEE)
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
    // DC 부담금
    // ─────────────────────────────────────────────────────────────
    // totalAnnual = 당해 연도 직원 전체 contribution 합계
    // N-1개 레코드는 base*(0.85~1.15) 랜덤, 마지막 레코드는 totalAnnual에서 나머지를 채워 합계 일치
    private void insertContributions(int year, PaymentCycle cycle, long totalAnnual, CompanyRetirementDc crd) {
        Random rnd = new Random();
        switch (cycle) {
            case MONTHLY -> {
                long base = totalAnnual / 12;
                long[] amounts = new long[12];
                long allocated = 0L;
                for (int i = 0; i < 11; i++) {
                    amounts[i] = Math.round(base * (0.85 + rnd.nextDouble() * 0.30));
                    allocated += amounts[i];
                }
                amounts[11] = totalAnnual - allocated;

                for (int month = 1; month <= 12; month++) {
                    LocalDate dueDate = LocalDate.of(year, month, 25);
                    String status = month < 6 ? "납입완료" : month == 6 ? "미납" : "예정";
                    contributionRepository.save(Contribution.builder()
                            .contributionAmount(amounts[month - 1])
                            .dueDate(dueDate)
                            .paidDate(month < 6 ? dueDate.minusDays(2) : null)
                            .status(status)
                            .cycle(PaymentCycle.MONTHLY)
                            .companyRetirementDc(crd)
                            .build());
                }
            }
            case QUARTERLY -> {
                long base = totalAnnual / 4;
                long[] amounts = new long[4];
                long allocated = 0L;
                for (int i = 0; i < 3; i++) {
                    amounts[i] = Math.round(base * (0.85 + rnd.nextDouble() * 0.30));
                    allocated += amounts[i];
                }
                amounts[3] = totalAnnual - allocated;

                int[][] quarters = {{3, 31}, {6, 30}, {9, 30}, {12, 31}};
                String[] statuses = {"납입완료", "미납", "예정", "예정"};
                for (int q = 0; q < 4; q++) {
                    LocalDate dueDate = LocalDate.of(year, quarters[q][0], quarters[q][1]);
                    contributionRepository.save(Contribution.builder()
                            .contributionAmount(amounts[q])
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
                            .contributionAmount(totalAnnual)
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
