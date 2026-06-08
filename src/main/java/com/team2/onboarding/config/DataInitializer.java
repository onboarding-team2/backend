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
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final CompanyRepository companyRepository;
    private final CompanyRetirementDcRepository companyRetirementDcRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeRetirementDcRepository employeeRetirementDcRepository;
    private final AnnualSalaryRepository annualSalaryRepository;
    private final AnnualSalaryUpdateRepository annualSalaryUpdateRepository;
    private final DefaultOptionHistoryRepository defaultOptionHistoryRepository;
    private final IrpAccountRepository irpAccountRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final MidWithdrawalRepository midWithdrawalRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final ContributionRepository contributionRepository;

    @Bean
    @Transactional
    CommandLineRunner initData() {
        return args -> {
            if (companyRepository.count() > 0) return;

            int year = LocalDate.now().getYear();

            String[][] companyData = {
                    {"1008100001", "테스트기업1", "김대표"},
                    {"1008100002", "테스트기업2", "이대표"},
                    {"1008100003", "테스트기업3", "박대표"},
            };

            String[][] employeeData = {
                    {"이재원", "900101"},
                    {"김민준", "850615"},
                    {"박지수", "920303"},
                    {"이수진", "780920"},
                    {"최민혁", "801212"},
                    {"정유진", "950430"},
            };

            for (int ci = 0; ci < companyData.length; ci++) {
                Company company = companyRepository.save(Company.builder()
                        .brn(companyData[ci][0])
                        .companyName(companyData[ci][1])
                        .representativeName(companyData[ci][2])
                        .password(passwordEncoder.encode("1234"))
                        .planType(PlanType.DC)
                        .build());

                CompanyRetirementDc crd = companyRetirementDcRepository.save(CompanyRetirementDc.builder()
                        .companyAccount("ACC-DC-" + (ci + 1))
                        .planType(PlanType.DC)
                        .contractDate(LocalDate.of(2022, 3, 1))
                        .feeDueDate(LocalDate.of(year, 12, 31))
                        .paymentCycle(PaymentCycle.MONTHLY)
                        .contributionDueDate(LocalDate.of(year, 6, 25))
                        .company(company)
                        .build());

                // 부담금 납입 이력: 1~5월 납입완료, 6월 미납, 7~12월 예정
                long baseContribution = 100_000_000L + (long) ci * 10_000_000L;
                for (int month = 1; month <= 12; month++) {
                    LocalDate dueDate = LocalDate.of(year, month, 25);
                    boolean paid = month < 6;
                    contributionRepository.save(Contribution.builder()
                            .contributionAmount(baseContribution)
                            .dueDate(dueDate)
                            .paidDate(paid ? dueDate.minusDays(2) : null)
                            .status(month < 6 ? "납입완료" : month == 6 ? "미납" : "예정")
                            .cycle(PaymentCycle.MONTHLY)
                            .companyRetirementDc(crd)
                            .build());
                }

                // 수수료 납입 이력: 운용관리 + 자산관리 각 6개월
                for (String feeType : new String[]{"운용관리", "자산관리"}) {
                    long feeAmount = "운용관리".equals(feeType) ? 500_000L : 300_000L;
                    for (int month = 1; month <= 6; month++) {
                        LocalDate dueDate = LocalDate.of(year, month, 15);
                        boolean paid = month <= 4;
                        feePaymentRepository.save(FeePayment.builder()
                                .feeAmount(feeAmount)
                                .feeType(feeType)
                                .dueDate(dueDate)
                                .paidDate(paid ? dueDate : null)
                                .status(paid ? "납입완료" : month == 5 ? "미납" : "예정")
                                .companyRetirementDc(crd)
                                .build());
                    }
                }

                // 직원 6명 생성
                for (int ei = 0; ei < employeeData.length; ei++) {
                    String rrn = employeeData[ei][1] + String.format("%07d", (ci * 6 + ei + 1));
                    LocalDate terminationDate = ei == 4
                            ? LocalDate.of(year, 8, 31)
                            : ei == 5 ? LocalDate.of(year, 7, 15) : null;

                    Employee employee = employeeRepository.save(Employee.builder()
                            .name(employeeData[ei][0])
                            .rrn(rrn)
                            .employeeType(ei == 3 ? EmployeeType.EXECUTIVE : EmployeeType.EMPLOYEE)
                            .startDate(LocalDate.of(2020, 1, 1))
                            .terminationDate(terminationDate)
                            .company(company)
                            .build());

                    boolean hasTermination = ei == 4 || ei == 5;

                    String defaultOption = switch (ei % 3) {
                        case 0 -> "Y";
                        case 1 -> "N";
                        default -> null;
                    };

                    EmployeeRetirementDc erd = employeeRetirementDcRepository.save(EmployeeRetirementDc.builder()
                            .employeeAccount("DC-" + (ci * 6 + ei + 1))
                            .accountType("DC")
                            .joinDate(LocalDate.of(2022, 4, 1))
                            .effectiveDate(LocalDate.of(2020, 1, 1))
                            .defaultOption(defaultOption)
                            .balance(20_000_000L + (long) (ei + 1) * 5_000_000L)
                            .employee(employee)
                            .companyRetirementDc(crd)
                            .build());

                    // 연간 임금 총액
                    for (int y = year - 1; y <= year; y++) {
                        AnnualSalary salary = annualSalaryRepository.save(AnnualSalary.builder()
                                .year(String.valueOf(y))
                                .salary(40_000_000L + (long) ei * 5_000_000L)
                                .employee(employee)
                                .build());

                        annualSalaryUpdateRepository.save(AnnualSalaryUpdate.builder()
                                .targetYear(String.valueOf(y))
                                .notifiedAt(LocalDateTime.of(y, 1, 10, 9, 0))
                                .deadline(LocalDate.of(y, 3, 31))
                                .status(y < year ? "갱신완료" : "갱신중")
                                .completedAt(y < year ? LocalDateTime.of(y, 2, 15, 14, 0) : null)
                                .annualSalary(salary)
                                .build());
                    }

                    // 디폴트옵션 이력
                    defaultOptionHistoryRepository.save(DefaultOptionHistory.builder()
                            .selectedOption("Y".equals(defaultOption) ? "MMF" : null)
                            .selectedDate("Y".equals(defaultOption) ? LocalDate.of(year - 1, 12, 1) : null)
                            .status("Y".equals(defaultOption) ? "선정" : "미선정")
                            .overdueDays("Y".equals(defaultOption) ? 0 : ei * 3)
                            .employeeRetirementDc(erd)
                            .build());

                    // 매수예정 처리 (직원별 2건)
                    purchaseOrderRepository.save(PurchaseOrder.builder()
                            .productName("삼성 MMF")
                            .productType("MMF")
                            .instructionDate(LocalDate.of(year, 1, 15))
                            .maturityDate(null)
                            .status("완료")
                            .employeeRetirementDc(erd)
                            .build());

                    purchaseOrderRepository.save(PurchaseOrder.builder()
                            .productName("국채 채권형펀드")
                            .productType("채권형펀드")
                            .instructionDate(LocalDate.of(year, 5, 10))
                            .maturityDate(LocalDate.of(year + 1, 5, 10))
                            .status("미완료")
                            .employeeRetirementDc(erd)
                            .build());

                    // 퇴직 예정자: IRP 계좌
                    if (hasTermination) {
                        irpAccountRepository.save(IrpAccount.builder()
                                .openedDate(ei == 4 ? LocalDate.of(year, 5, 20) : null)
                                .status(ei == 4 ? "개설완료" : "미완료")
                                .terminationDate(terminationDate)
                                .delayStatus(ei == 5 ? "확인필요" : null)
                                .employeeRetirementDc(erd)
                                .build());
                    }

                    // 중도인출 (직원당 1명)
                    if (ei == 2) {
                        midWithdrawalRepository.save(MidWithdrawal.builder()
                                .requestDate(LocalDate.of(year, 3, 5))
                                .status("완료")
                                .amount(5_000_000L)
                                .reason("주택 구입")
                                .employeeRetirementDc(erd)
                                .build());
                    }
                }
            }

            System.out.println("=== DC Mock Data Insert Complete ===");
        };
    }
}
