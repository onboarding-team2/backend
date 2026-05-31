package com.team2.onboarding.config;

import com.team2.onboarding.entity.*;
import com.team2.onboarding.enums.EmployeeType;
import com.team2.onboarding.enums.PaymentCycle;
import com.team2.onboarding.enums.PlanType;
import com.team2.onboarding.enums.RetirementType;
import com.team2.onboarding.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CompanyRepository companyRepository;
    private final CompanyRetirementRepository companyRetirementRepository;
    private final InvestmentProductRepository investmentProductRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeRetirementRepository employeeRetirementRepository;
    private final AnnualSalaryRepository annualSalaryRepository;
    private final ContributionRepository contributionRepository;

    @Bean
    @Transactional
    CommandLineRunner initData() {
        return args -> {
            if (companyRepository.count() > 0) {
                return;
            }

            int currentYear = LocalDate.now().getYear();
            int currentMonth = LocalDate.now().getMonthValue();

            List<Company> companies = new ArrayList<>();

            for (int i = 3; i <= 15; i++) {
                boolean isDc = i % 2 != 0;

                Company company = companyRepository.save(
                        Company.builder()
                                .companyId(String.format("C%03d", i))
                                .brn(String.format("%012d", 100000000000L + i))
                                .companyName("테스트기업" + i)
                                .representativeName("대표" + i)
                                .password("1234")
                                .build()
                );

                companies.add(company);

                companyRetirementRepository.save(
                        CompanyRetirement.builder()
                                .companyAccount("ACC-" + i)
                                .planType(isDc ? PlanType.DC : PlanType.DB)
                                .contractDate(LocalDate.of(
                                        2020 + (i % 5),
                                        (i % 12) + 1,
                                        (i % 27) + 1
                                ))
                                .feeDueDate(LocalDate.of(currentYear, 12, 31))
                                .paymentCycle(
                                        switch (i % 3) {
                                            case 0 -> PaymentCycle.MONTHLY;
                                            case 1 -> PaymentCycle.QUARTERLY;
                                            default -> PaymentCycle.YEARLY;
                                        }
                                )
                                .contributionDueDate(LocalDate.of(currentYear, 12, 31))
                                .company(company)
                                .build()
                );

                investmentProductRepository.save(
                        InvestmentProduct.builder()
                                .company(company)
                                .build()
                );

                if (isDc) {
                    // DC 회사: 현재 연도 1월~전월까지 납입 기록 생성 (당월은 미납)
                    long[] monthlyAmounts = {
                            115_000_000L, 122_000_000L, 118_000_000L, 125_000_000L,
                            119_000_000L, 121_000_000L, 116_000_000L, 123_000_000L,
                            120_000_000L, 117_000_000L, 124_000_000L
                    };
                    int paidMonths = Math.min(currentMonth - 1, 11);
                    for (int month = 1; month <= paidMonths; month++) {
                        long amount = monthlyAmounts[month - 1];
                        // 회사마다 약간 다른 금액
                        amount = amount + (long) ((i - 3) * 1_000_000);
                        contributionRepository.save(
                                Contribution.builder()
                                        .contributionAmount(amount)
                                        .paidDate(LocalDate.of(currentYear, month, 25))
                                        .company(company)
                                        .build()
                        );
                    }
                } else {
                    // DB 회사: 단건 납입 기록
                    contributionRepository.save(
                            Contribution.builder()
                                    .contributionAmount((long) (Math.random() * 80_000_000))
                                    .paidDate(LocalDate.of(
                                            currentYear - 1,
                                            (i % 12) + 1,
                                            (i % 27) + 1
                                    ))
                                    .company(company)
                                    .build()
                    );
                }
            }

            /*
             * 직원 자동 생성
             */
            int memberNo = 4;

            for (int ci = 0; ci < companies.size(); ci++) {
                Company company = companies.get(ci);
                boolean isDc = (ci + 3) % 2 != 0;

                for (int j = 0; j < 6; j++) {

                    Employee employee = employeeRepository.save(
                            Employee.builder()
                                    .memberId(String.format("M%03d", memberNo))
                                    .name("직원" + memberNo)
                                    .rrn("900101" + String.format("%07d", memberNo))
                                    .company(company)
                                    .build()
                    );

                    // DC 첫 번째 회사(C003)의 처음 3명에게 퇴직 예정일 설정
                    LocalDate terminationDate = null;
                    RetirementType retirementType = null;
                    if (ci == 0 && isDc) {
                        if (j == 0) {
                            terminationDate = LocalDate.of(currentYear, 6, 15);
                            retirementType = RetirementType.MANDATORY;
                        } else if (j == 1) {
                            terminationDate = LocalDate.of(currentYear, 7, 20);
                            retirementType = RetirementType.VOLUNTARY;
                        } else if (j == 2) {
                            terminationDate = LocalDate.of(currentYear, 8, 1);
                            retirementType = RetirementType.MANDATORY;
                        }
                    }

                    employeeRetirementRepository.save(
                            EmployeeRetirement.builder()
                                    .employeeAccount("IRP-" + memberNo)
                                    .joinDate(LocalDate.of(
                                            2020 + (memberNo % 5),
                                            (memberNo % 12) + 1,
                                            (memberNo % 27) + 1
                                    ))
                                    .startDate(LocalDate.of(2020 + (memberNo % 5), 1, 1))
                                    .terminationDate(terminationDate)
                                    .effectiveDate(LocalDate.now())
                                    .defaultOption(memberNo % 2 == 0)
                                    .employeeType(memberNo % 5 == 0
                                            ? EmployeeType.EXECUTIVE
                                            : EmployeeType.EMPLOYEE)
                                    .balance(5_000_000L + (long) (Math.random() * 150_000_000))
                                    .retirementType(retirementType)
                                    .employee(employee)
                                    .build()
                    );

                    annualSalaryRepository.saveAll(List.of(
                            AnnualSalary.builder()
                                    .year(String.valueOf(currentYear - 1))
                                    .salary(35_000_000L + (long) (Math.random() * 70_000_000))
                                    .employee(employee)
                                    .build(),
                            AnnualSalary.builder()
                                    .year(String.valueOf(currentYear))
                                    .salary(40_000_000L + (long) (Math.random() * 90_000_000))
                                    .employee(employee)
                                    .build()
                    ));

                    memberNo++;
                }
            }

            System.out.println("=== Mock Data Insert Complete ===");
        };
    }
}
