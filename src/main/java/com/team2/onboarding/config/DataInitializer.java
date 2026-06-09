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

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final CompanyRepository companyRepository;
    private final CompanyRetirementDcRepository companyRetirementDcRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeRetirementDcRepository employeeRetirementDcRepository;
    private final AnnualSalaryRepository annualSalaryRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final ContributionRepository contributionRepository;

    @Bean
    @Transactional
    CommandLineRunner initData() {
        return args -> {
            if (companyRepository.count() > 0) return;

            int year = LocalDate.now().getYear();

            // 7개 회사: DC×5 + DB×2, PaymentCycle - MONTHLY×3, QUARTERLY×3, YEARLY×1
            String[][] companyData = {
                    {"1008100001", "삼성전자",   "이재용"},
                    {"1008100002", "현대자동차", "정의선"},
                    {"1008100003", "카카오",     "홍은택"},
                    {"1008100004", "네이버",     "최수연"},
                    {"1008100005", "LG전자",     "조주완"},
                    {"1008100006", "포스코",     "최정우"},
                    {"1008100007", "SK하이닉스", "곽노정"},
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
                    PaymentCycle.MONTHLY,
                    PaymentCycle.QUARTERLY,
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

            // 10명 직원: ei 0-1 임원, ei 2-7 사원, ei 8-9 퇴직자
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

            // default_option: Y / N / null 모두 포함
            String[] defaultOptions = {"Y", "N", "Y", "N", "Y", null, "N", "Y", "N", "N"};

            for (int ci = 0; ci < companyData.length; ci++) {

                Company company = companyRepository.save(Company.builder()
                        .brn(companyData[ci][0])
                        .companyName(companyData[ci][1])
                        .representativeName(companyData[ci][2])
                        .password(passwordEncoder.encode("1234"))
                        .planType(planTypes[ci])
                        .build());

                PaymentCycle cycle = cycles[ci];
                LocalDate contractDate = contractDates[ci];

                CompanyRetirementDc crd = companyRetirementDcRepository.save(CompanyRetirementDc.builder()
                        .companyAccount(String.format("ACC-%s-%03d", planTypes[ci].name(), ci + 1))
                        .planType(planTypes[ci])
                        .contractDate(contractDate)
                        .paymentCycle(cycle)
                        .company(company)
                        .build());

                // ── 부담금 납입 이력 ─────────────────────────────────
                long baseContribution = 80_000_000L + (long) ci * 20_000_000L;
                insertContributions(year, cycle, baseContribution, crd);

                // ── 수수료 납입 이력 (연도별 1건 × 2종류) ────────────
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

                // ── 직원 10명 ─────────────────────────────────────────
                for (int ei = 0; ei < employeeData.length; ei++) {
                    int globalIdx = ci * 10 + ei;
                    String rrn = employeeData[ei][1] + String.format("%07d", globalIdx + 1);

                    boolean isTerminated = ei >= 8;
                    LocalDate terminationDate = !isTerminated ? null
                            : (ei == 8
                                    ? LocalDate.of(year - 1, 8, 31)
                                    : LocalDate.of(year - 1, 7, 15));

                    EmployeeType empType = ei < 2 ? EmployeeType.EXECUTIVE : EmployeeType.EMPLOYEE;

                    Employee employee = employeeRepository.save(Employee.builder()
                            .name(employeeData[ei][0])
                            .rrn(rrn)
                            .employeeType(empType)
                            .startDate(LocalDate.of(2018 + ci, 3, 1))
                            .terminationDate(terminationDate)
                            .company(company)
                            .build());

                    employeeRetirementDcRepository.save(EmployeeRetirementDc.builder()
                            .employeeAccount(String.format("DC-%04d", globalIdx + 1))
                            .accountType("DC")
                            .joinDate(LocalDate.of(2018 + ci, 4, 1))
                            .effectiveDate(LocalDate.of(2018 + ci, 3, 1))
                            .defaultOption(defaultOptions[ei])
                            .hasIrpAccount(isTerminated ? "Y" : "N")
                            .employee(employee)
                            .companyRetirementDc(crd)
                            .build());

                    // ── 연간 임금 총액 ────────────────────────────────
                    long baseSalary = empType == EmployeeType.EXECUTIVE
                            ? 100_000_000L + (long) (ei % 2) * 20_000_000L
                            : 40_000_000L  + (long) (ei - 2) * 5_000_000L;

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

            System.out.println("=== DC Mock Data Insert Complete ===");
        };
    }

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
