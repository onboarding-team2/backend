package com.team2.onboarding.config;

import com.team2.onboarding.entity.*;
import com.team2.onboarding.enums.PaymentCycle;
import com.team2.onboarding.enums.PlanType;
import com.team2.onboarding.enums.EmployeeType; // [수정] EmployeeType Enum 패키지 임포트 확인
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
    @Transactional // [수정] CommandLineRunner 실행 전체가 하나의 트랜잭션으로 묶이도록 Bean에 선언하는 것이 안전합니다.
    CommandLineRunner initData() {
        return args -> {
            if (companyRepository.count() > 0) {
                return;
            }

            /*
             * 추가 회사 대량 생성
             */
            List<Company> companies = new ArrayList<>();

            for (int i = 3; i <= 15; i++) {

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
                                .planType(i % 2 == 0 ? PlanType.DB : PlanType.DC)
                                .contractDate(LocalDate.of(
                                        2020 + (i % 5),
                                        (i % 12) + 1,
                                        (i % 27) + 1
                                ))
                                .feeDueDate(LocalDate.of(2025, 12, 31))
                                .paymentCycle(
                                        switch (i % 3) {
                                            case 0 -> PaymentCycle.MONTHLY;
                                            case 1 -> PaymentCycle.QUARTERLY;
                                            default -> PaymentCycle.YEARLY;
                                        }
                                )
                                .contributionDueDate(LocalDate.of(2025, 12, 31))
                                .company(company)
                                .build()
                );

                investmentProductRepository.save(
                        InvestmentProduct.builder()
                                .company(company)
                                .build()
                );

                contributionRepository.save(
                        Contribution.builder()
                                .contributionAmount(
                                        (long) (Math.random() * 80_000_000)
                                )
                                .paidDate(LocalDate.of(
                                        2025,
                                        (i % 12) + 1,
                                        (i % 27) + 1
                                ))
                                .company(company)
                                .build()
                );
            }

            /*
             * 직원 80명 자동 생성
             */
            List<Employee> employees = new ArrayList<>();

            int memberNo = 4;

            for (Company company : companies) {

                for (int j = 0; j < 6; j++) {

                    Employee employee =
                            employeeRepository.save(
                                    Employee.builder()
                                            .memberId(
                                                    String.format(
                                                            "M%03d",
                                                            memberNo
                                                    )
                                            )
                                            .name("직원" + memberNo)
                                            .rrn(
                                                    "900101"
                                                            + String.format(
                                                            "%07d",
                                                            memberNo
                                                    )
                                            )
                                            .company(company)
                                            .build()
                            );

                    employees.add(employee);

                    employeeRetirementRepository.save(
                            EmployeeRetirement.builder()
                                    .employeeAccount(
                                            "IRP-" + memberNo
                                    )
                                    .joinDate(LocalDate.of(
                                            2020 + (memberNo % 5),
                                            (memberNo % 12) + 1,
                                            (memberNo % 27) + 1
                                    ))
                                    .startDate(LocalDate.of(
                                            2020 + (memberNo % 5),
                                            1,
                                            1
                                    ))
                                    .terminationDate(null)
                                    .effectiveDate(LocalDate.now())
                                    .defaultOption(
                                            memberNo % 2 == 0
                                    )
                                    .employeeType(
                                            memberNo % 5 == 0
                                                    ? EmployeeType.EXECUTIVE
                                                    : EmployeeType.EMPLOYEE
                                    )
                                    .balance(
                                            5_000_000L
                                                    + (long)
                                                    (Math.random()
                                                            * 150_000_000)
                                    )
                                    .employee(employee)
                                    .build()
                    );

                    annualSalaryRepository.saveAll(List.of(

                            AnnualSalary.builder()
                                    .year("2024")
                                    .salary(
                                            35_000_000L
                                                    + (long)
                                                    (Math.random()
                                                            * 70_000_000)
                                    )
                                    .employee(employee)
                                    .build(),

                            AnnualSalary.builder()
                                    .year("2025")
                                    .salary(
                                            40_000_000L
                                                    + (long)
                                                    (Math.random()
                                                            * 90_000_000)
                                    )
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