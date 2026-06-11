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

    // ── 일정 리포지토리 ───────────────────────────────────────────
    private final DbScheduleRepository dbScheduleRepository;
    private final DcScheduleRepository dcScheduleRepository;

    // ── DB 리포지토리 ─────────────────────────────────────────────
    private final CompanyRetirementDbRepository companyRetirementDbRepository;
    private final EmployeeRetirementDbRepository employeeRetirementDbRepository;
    private final AnnualSalaryDbRepository annualSalaryDbRepository;
    private final FeePaymentDbRepository feePaymentDbRepository;
    private final ReserveDbRepository reserveDbRepository;
    private final AssetClassMasterRepository assetClassMasterRepository;
    private final InvestmentProductMasterRepository investmentProductMasterRepository;
    private final InvestmentProductDbRepository investmentProductDbRepository;
    private final ReturnHistoryDbRepository returnHistoryDbRepository;

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
            List<AssetClassMaster> assetClasses = createAssetClassMasters();
            List<InvestmentProductMaster> productMasters = createProductMasters(assetClasses);

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
                LocalDate feeDue = contractDate.withYear(y).plusMonths(1);
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
        insertDcSchedules(company, crd, year);
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
                .targetReturnRate(new BigDecimal("3.50"))
                .company(company)
                .build());

        // 수수료 납입 이력
        for (String feeType : new String[]{"운용관리", "자산관리"}) {
            long feeAmount = "운용관리".equals(feeType)
                    ? feeBaseMan * 10_000L
                    : (long) Math.round(feeBaseMan * 10_000L * 0.6);
            for (int y = year - 2; y <= year; y++) {
                boolean paid = y < year;
                LocalDate feeDue = contractDate.withYear(y).plusMonths(1);
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
                portfolioType, crdb, productMasters, totalBenefitObligation, targetFundingRatio, year);
        insertReserve(crdb, baseDate, totalBenefitObligation, fundedAmount);
        insertReturnHistory(crdb, fundedAmount, year);
        insertDbSchedules(company, crdb, year);
    }

    // ─────────────────────────────────────────────────────────────
    // 운용상품 매수 — 포트폴리오 유형별 상품 구성 + targetFundingRatio 스케일링
    // masterIdx: 0=IBK정기예금, 1=IBK GIC, 2=ELB, 3=KODEX회사채, 4=ACE국고채,
    //            5=국공채펀드, 6=TIGER TDF2040, 7=KODEX TRF3070, 8=한국밸런스펀드,
    //            9=KODEX200, 10=TIGER코스피, 11=KODEX코스닥150, 12=ACE배당성장,
    //           13=TIGER나스닥100, 14=KODEX S&P500, 15=SOL배당다우존스,
    //           16=ACE미국테크TOP10, 17=TIGER차이나전기차
    // yearOffset: null=ETF(만기없음), 0=당해연도, 1=익년, -1=전년(만기완료)
    // ─────────────────────────────────────────────────────────────
    private long insertInvestmentProducts(int portfolioType, CompanyRetirementDb crdb,
                                          List<InvestmentProductMaster> masters,
                                          long totalBenefitObligation,
                                          double targetFundingRatio,
                                          int year) {
        // {masterIdx, 비율(%), yearOffset(null=ETF무만기/0=올해/1=내년/-1=작년), month, day, status}
        Object[][] portfolio = switch (portfolioType) {
            case 0 -> new Object[][]{ // 보수형 — 원리금보장형만
                    {0, 50.0,  0, 12, 31, "운용중"},   // IBK 정기예금 1년 — 올해 12월 만기
                    {1, 30.0,  1,  6, 30, "운용중"},   // IBK GIC 2년 — 내년 6월 만기
                    {2, 20.0, -1,  3, 31, "만기완료"}, // ELB 지수연계형 — 작년 3월 만기완료
            };
            case 1 -> new Object[][]{ // 균형형 — 원리금보장형 + 채권 + 혼합
                    {0, 25.0,  0, 11, 30, "운용중"},        // IBK 정기예금 1년 — 올해 11월 만기
                    {1, 15.0,  1,  5, 31, "운용중"},        // IBK GIC 2년 — 내년 5월 만기
                    {3, 25.0, null, null, null, "운용중"},   // KODEX 회사채 ETF
                    {6, 20.0, null, null, null, "운용중"},   // TIGER TDF2040
                    {7, 15.0, null, null, null, "운용중"},   // KODEX TRF3070
            };
            case 2 -> new Object[][]{ // 성장형 — 원리금보장형 + 채권 + 국내외주식
                    {0,  15.0,  0,  9, 30, "운용중"},       // IBK 정기예금 1년 — 올해 9월 만기
                    {3,  20.0, null, null, null, "운용중"},  // KODEX 회사채 ETF
                    {9,  30.0, null, null, null, "운용중"},  // KODEX 200
                    {13, 20.0, null, null, null, "운용중"},  // TIGER 미국나스닥100
                    {14, 15.0, null, null, null, "운용중"},  // KODEX 미국S&P500TR
            };
            default -> new Object[][]{ // 혼합형 — 만기완료 포함
                    {0,  20.0,  0,  8, 31, "운용중"},       // IBK 정기예금 — 올해 8월 만기
                    {4,  17.5, null, null, null, "운용중"},  // ACE 국고채10년
                    {7,  22.5, null, null, null, "운용중"},  // KODEX TRF3070
                    {9,  25.0, null, null, null, "운용중"},  // KODEX 200
                    {2,  15.0, -1,  6, 30, "만기완료"},     // ELB 지수연계형 — 작년 6월 만기완료
            };
        };

        double normalizedFundedRatio = 0.0;
        for (Object[] item : portfolio) {
            int masterIdx = (int) item[0];
            double ratio = (double) item[1];
            double rate = masters.get(masterIdx).getAnnualReturnRate().doubleValue();
            normalizedFundedRatio += ratio / 100.0 * (1.0 + rate / 100.0);
        }
        double scalingFactor = (targetFundingRatio / 100.0) / normalizedFundedRatio;

        long totalFunded = 0L;
        for (Object[] item : portfolio) {
            int masterIdx      = (int)    item[0];
            double ratio       = (double) item[1];
            Integer yearOffset = (Integer) item[2];
            String status      = (String) item[5];

            long principal = Math.round(totalBenefitObligation * ratio / 100.0 * scalingFactor);
            InvestmentProductMaster master = masters.get(masterIdx);
            BigDecimal rate = master.getAnnualReturnRate();

            LocalDate maturityDate;
            if (yearOffset == null) {
                maturityDate = null;
            } else {
                int matMonth = (Integer) item[3];
                int matDay   = (Integer) item[4];
                maturityDate = LocalDate.of(year + yearOffset, matMonth, matDay);
            }

            long evaluatedAmount = Math.round(principal * (1 + rate.doubleValue() / 100.0));
            Long confirmedAmount = "만기완료".equals(status) ? evaluatedAmount : null;
            totalFunded += evaluatedAmount;

            investmentProductDbRepository.save(InvestmentProductDb.builder()
                    .principal(principal)
                    .annualReturnRate(rate)
                    .purchaseDate(LocalDate.now().minusMonths(12))
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
    // 월별 수익률 이력 — 36개월 (3년치 차트용)
    // 36번째 포인트가 currentFundedAmount, 역산으로 이전 시점 적립금 생성
    // ─────────────────────────────────────────────────────────────
    private void insertReturnHistory(CompanyRetirementDb crdb, long currentFundedAmount, int year) {
        double targetAnnualReturn = crdb.getTargetReturnRate().doubleValue();
        Random rnd = new Random(crdb.getId());

        double[] returnRates = new double[36];
        double[] growthFactors = new double[36];
        double compoundGrowth = 1.0;

        for (int i = 0; i < 36; i++) {
            double noise = (rnd.nextDouble() - 0.5) * 2.0; // ±1% 연환산 노이즈
            returnRates[i] = Math.max(0.5, targetAnnualReturn + noise);
            growthFactors[i] = 1.0 + returnRates[i] / 100.0 / 12.0;
            compoundGrowth *= growthFactors[i];
        }

        long startingAmount = Math.round(currentFundedAmount / compoundGrowth);
        long[] fundedAmounts = new long[36];
        fundedAmounts[0] = startingAmount;
        for (int i = 1; i < 35; i++) {
            fundedAmounts[i] = Math.round(fundedAmounts[i - 1] * growthFactors[i]);
        }
        fundedAmounts[35] = currentFundedAmount; // 최종값 고정

        LocalDate latestDate = LocalDate.of(year - 1, 12, 31);
        for (int i = 0; i < 36; i++) {
            LocalDate baseDate = latestDate.minusMonths(35 - i);
            baseDate = baseDate.withDayOfMonth(baseDate.lengthOfMonth());
            returnHistoryDbRepository.save(ReturnHistoryDb.builder()
                    .baseDate(baseDate)
                    .returnRate(BigDecimal.valueOf(returnRates[i]).setScale(2, RoundingMode.HALF_UP))
                    .fundedAmount(fundedAmounts[i])
                    .companyRetirementDb(crdb)
                    .build());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 상품군 마스터 (asset_class_master)
    // ─────────────────────────────────────────────────────────────
    private List<AssetClassMaster> createAssetClassMasters() {
        // {classCode, className, isRiskAsset, allowMulti, avgReturn3y, displayOrder}
        Object[][] data = {
                {"DEPOSIT", "원리금보장형", false, false, "3.70", 1},
                {"BOND",    "채권형",       false, false, "4.90", 2},
                {"MIXED",   "혼합형/TDF",   true,  true,  "8.80", 3},
                {"DOM_EQ",  "국내주식형",   true,  true,  "9.20", 4},
                {"OVS_EQ",  "해외주식형",   true,  true, "16.20", 5},
        };
        List<AssetClassMaster> result = new ArrayList<>();
        for (Object[] row : data) {
            result.add(assetClassMasterRepository.save(AssetClassMaster.builder()
                    .classCode((String) row[0])
                    .className((String) row[1])
                    .isRiskAsset((Boolean) row[2])
                    .allowMulti((Boolean) row[3])
                    .avgReturn3y(new BigDecimal((String) row[4]))
                    .displayOrder((Integer) row[5])
                    .build()));
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────
    // 운용상품 마스터 (investment_product_master) — 18개
    // ─────────────────────────────────────────────────────────────
    private List<InvestmentProductMaster> createProductMasters(List<AssetClassMaster> ac) {
        // {productName, provider, assetClassIdx, category, isPrinGuaranteed, annualRate, rate3y, feeRate, tag, isDefault}
        Object[][] data = {
            // ── DEPOSIT (ac[0]) ──────────────────────────────────────────────────────
            {"IBK 정기예금 1년",            "IBK기업은행",      0, "정기예금",       true,  "3.60", "3.60", "0.0000", "기본",       true},  // 0
            {"IBK GIC 2년",                 "IBK연금보험",      0, "이율보증형보험", true,  "3.40", "3.40", "0.0000", null,         false}, // 1
            {"ELB 지수연계형",              "IBK투자증권",      0, "ELB 및 ELD",     true,  "4.10", "4.10", "0.0000", "수익률 1위", false}, // 2
            // ── BOND (ac[1]) ─────────────────────────────────────────────────────────
            {"KODEX 28-12 회사채(AA-)",     "삼성자산운용",     1, "ETF",            false, "5.80", "5.80", "0.0900", "수익률 1위", true},  // 3
            {"ACE 국고채10년",              "한국투자신탁운용", 1, "ETF",            false, "4.90", "4.90", "0.0500", "안정",       false}, // 4
            {"국공채 펀드",                 "IBK자산운용",      1, "펀드",           false, "4.00", "4.00", "0.2500", null,         false}, // 5
            // ── MIXED (ac[2]) ────────────────────────────────────────────────────────
            {"TIGER 글로벌멀티에셋TDF2040", "미래에셋자산운용", 2, "ETF",            false, "11.30","11.30","0.3900", "TDF",        true},  // 6
            {"KODEX TRF3070",               "삼성자산운용",     2, "ETF",            false, "8.40", "8.40", "0.2400", "안정혼합",   false}, // 7
            {"한국밸런스 혼합펀드",         "한국투자신탁운용", 2, "펀드",           false, "6.80", "6.80", "0.4500", null,         false}, // 8
            // ── DOM_EQ (ac[3]) ───────────────────────────────────────────────────────
            {"KODEX 200",                   "삼성자산운용",     3, "ETF",            false, "8.20", "8.20", "0.1500", "대표지수",   true},  // 9
            {"TIGER 코스피",                "미래에셋자산운용", 3, "ETF",            false, "7.90", "7.90", "0.1500", null,         false}, // 10
            {"KODEX 코스닥150",             "삼성자산운용",     3, "ETF",            false, "11.40","11.40","0.2500", "수익률 1위", false}, // 11
            {"ACE 배당성장",                "한국투자신탁운용", 3, "ETF",            false, "9.10", "9.10", "0.1500", "배당",       false}, // 12
            // ── OVS_EQ (ac[4]) ──────────────────────────────────────────────────────
            {"TIGER 미국나스닥100",         "미래에셋자산운용", 4, "ETF",            false, "21.50","21.50","0.0700", "수익률 1위", true},  // 13
            {"KODEX 미국S&P500TR",          "삼성자산운용",     4, "ETF",            false, "17.20","17.20","0.0099", "보수 최저",  true},  // 14
            {"SOL 미국배당다우존스",        "신한자산운용",     4, "ETF",            false, "11.90","11.90","0.0900", "월배당",     false}, // 15
            {"ACE 미국테크TOP10",           "한국투자신탁운용", 4, "ETF",            false, "24.10","24.10","0.1000", "고변동",     false}, // 16
            {"TIGER 차이나전기차",          "미래에셋자산운용", 4, "ETF",            false, "6.20", "6.20", "0.4900", null,         false}, // 17
        };
        List<InvestmentProductMaster> result = new ArrayList<>();
        for (Object[] row : data) {
            result.add(investmentProductMasterRepository.save(InvestmentProductMaster.builder()
                    .productName((String)   row[0])
                    .productProvider((String) row[1])
                    .assetClass(ac.get((int) row[2]))
                    .productCategory((String) row[3])
                    .isPrincipalGuaranteed((Boolean) row[4])
                    .annualReturnRate(new BigDecimal((String) row[5]))
                    .returnRate3y(new BigDecimal((String) row[6]))
                    .feeRate(new BigDecimal((String) row[7]))
                    .productTag((String) row[8])
                    .isDefault((Boolean) row[9])
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
    // DB 일정 생성
    //  ① 수수료 납입 (운용관리·자산관리) — 계약응당일 기준 매년
    //  ② 적립금 납입 — 매년 12/15
    //  ③ 보유상품 만기 도래 — 당해 연도 만기인 운용중 상품 전체
    // ─────────────────────────────────────────────────────────────
    private void insertDbSchedules(Company company, CompanyRetirementDb crdb, int year) {
        LocalDate today = LocalDate.now();
        LocalDate contractDate = crdb.getContractDate();

        // ① 수수료 납입 (운용관리 / 자산관리) — 계약응당일 기준 매년 갱신
        for (String feeType : new String[]{"운용관리", "자산관리"}) {
            LocalDate feeDue = contractDate.withYear(year).plusMonths(1);
            String feeStatus = feeDue.isBefore(today) ? "OVERDUE" : "ACTIVE";
            dbScheduleRepository.save(DbSchedule.builder()
                    .title(feeType + " 수수료 납입")
                    .dueDate(feeDue)
                    .description("연간 " + feeType + " 수수료 납입 기한 (계약응당일 기준)")
                    .status(feeStatus)
                    .company(company)
                    .build());
        }

        // ② 적립금 납입 — 매년 12/15
        dbScheduleRepository.save(DbSchedule.builder()
                .title("적립금 납입")
                .dueDate(LocalDate.of(year, 12, 15))
                .description("당해 연도 확정급여 적립금 납입 기한 (매년 12월 15일)")
                .status("ACTIVE")
                .company(company)
                .build());

        // ③ 보유상품 만기 도래 — investment_products_db 중 올해 만기인 상품 (만기 임박 순)
        investmentProductDbRepository.findWithProductMasterByCompanyRetirementDb_Id(crdb.getId()).stream()
                .filter(p -> "운용중".equals(p.getStatus())
                        && p.getMaturityDate() != null
                        && p.getMaturityDate().getYear() == year)
                .sorted(java.util.Comparator.comparing(InvestmentProductDb::getMaturityDate))
                .forEach(p -> {
                    String matStatus = p.getMaturityDate().isBefore(today) ? "OVERDUE" : "ACTIVE";
                    String productName = p.getProductMaster().getProductProvider()
                            + " " + p.getProductMaster().getProductName();
                    dbScheduleRepository.save(DbSchedule.builder()
                            .title(productName + " 만기 도래")
                            .dueDate(p.getMaturityDate())
                            .description(productName + " 만기 도래 — 재투자 또는 상품 전환 검토 필요")
                            .status(matStatus)
                            .company(company)
                            .build());
                });
    }

    // ─────────────────────────────────────────────────────────────
    // DC 일정 생성
    //  ① 수수료 납입 (운용관리·자산관리) — fee_payments_dc.due_date 기준 (계약응당일)
    //  ② 부담금 납입 — contributions_dc.due_date 기준 (납입주기별 미납/예정 항목)
    //  ③ 연간임금총액 갱신 — 매년 12/15
    // ─────────────────────────────────────────────────────────────
    private void insertDcSchedules(Company company, CompanyRetirementDc crd, int year) {
        LocalDate today = LocalDate.now();
        LocalDate contractDate = crd.getContractDate();
        PaymentCycle cycle = crd.getPaymentCycle();

        // ① 수수료 납입 (운용관리 / 자산관리)
        for (String feeType : new String[]{"운용관리", "자산관리"}) {
            LocalDate feeDue = contractDate.withYear(year).plusMonths(1);
            String feeStatus = feeDue.isBefore(today) ? "OVERDUE" : "ACTIVE";
            dcScheduleRepository.save(DcSchedule.builder()
                    .title(feeType + " 수수료 납입")
                    .dueDate(feeDue)
                    .description("연간 " + feeType + " 수수료 납입 기한 (계약응당일 기준)")
                    .status(feeStatus)
                    .company(company)
                    .build());
        }

        // ② 적립금 납입 — 납입 주기별 미납/예정 항목
        switch (cycle) {
            case MONTHLY -> {
                // 6월(미납)·7월(예정) 두 건 — 현재 기준 가장 임박한 2개월
                for (int m = 6; m <= 7; m++) {
                    LocalDate due = LocalDate.of(year, m, 25);
                    String s = due.isBefore(today) ? "OVERDUE" : "ACTIVE";
                    dcScheduleRepository.save(DcSchedule.builder()
                            .title("부담금 납입 - " + m + "월")
                            .dueDate(due)
                            .description(year + "년 " + m + "월분 DC 부담금 납입 (월납)")
                            .status(s)
                            .company(company)
                            .build());
                }
            }
            case QUARTERLY -> {
                // Q2(6/30 미납) · Q3(9/30 예정)
                int[][] quarters = {{6, 30}, {9, 30}};
                String[] qLabels = {"2분기", "3분기"};
                for (int q = 0; q < 2; q++) {
                    LocalDate due = LocalDate.of(year, quarters[q][0], quarters[q][1]);
                    String s = due.isBefore(today) ? "OVERDUE" : "ACTIVE";
                    dcScheduleRepository.save(DcSchedule.builder()
                            .title("부담금 납입 - " + qLabels[q])
                            .dueDate(due)
                            .description(year + "년 " + qLabels[q] + " DC 부담금 납입 (분기납)")
                            .status(s)
                            .company(company)
                            .build());
                }
            }
            case YEARLY -> {
                // 전년도 미납(OVERDUE) + 당해 연도 예정(ACTIVE)
                dcScheduleRepository.save(DcSchedule.builder()
                        .title("연간 적립금 납입 - " + (year - 1) + "년")
                        .dueDate(LocalDate.of(year - 1, 12, 31))
                        .description((year - 1) + "년 연간 DC 부담금 미납 — 즉시 납입 필요")
                        .status("OVERDUE")
                        .company(company)
                        .build());
                dcScheduleRepository.save(DcSchedule.builder()
                        .title("연간 적립금 납입 - " + year + "년")
                        .dueDate(LocalDate.of(year, 12, 31))
                        .description(year + "년 연간 DC 부담금 납입 예정 (연납)")
                        .status("ACTIVE")
                        .company(company)
                        .build());
            }
        }

        // ③ 연간임금총액 갱신 — 매년 12/15
        dcScheduleRepository.save(DcSchedule.builder()
                .title("연간임금총액 갱신")
                .dueDate(LocalDate.of(year, 12, 15))
                .description("당해 연도 연간임금총액 확정 및 퇴직연금 기여율 재산정")
                .status("ACTIVE")
                .company(company)
                .build());
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
