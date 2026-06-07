package com.team2.onboarding.config;

import com.team2.onboarding.entity.*;
import com.team2.onboarding.enums.EmployeeType;
import com.team2.onboarding.enums.PaymentCycle;
import com.team2.onboarding.enums.PlanType;
import com.team2.onboarding.enums.RetirementType;
import com.team2.onboarding.enums.ScheduleStatus;
import com.team2.onboarding.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final CompanyRepository companyRepository;
    private final CompanyRetirementRepository companyRetirementRepository;
    private final InvestmentProductRepository investmentProductRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeRetirementRepository employeeRetirementRepository;
    private final AnnualSalaryRepository annualSalaryRepository;
    private final ContributionRepository contributionRepository;
    private final ScheduleRepository scheduleRepository;

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
                                .companyName("테스트기업" + i)
                                .representativeName("대표" + i)
                                .password(passwordEncoder.encode("1234"))
                                .build()
                );
                company.updateIdentifiers(
                        String.format("C%05d", company.getId()),
                        String.format("10081%05d", company.getId())
                );
                companyRepository.save(company);

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
            List<Employee> employees = new ArrayList<>();
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

                    employees.add(employee);

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

            /*
             * 기일관리 일정 30개 생성
             */
            List<CompanyRetirement> allRetirements = companyRetirementRepository.findAll();

            String[] titles = {
                    "가입자 퇴직 정산 처리", "DC형 운용지시 기한 도래", "DB형 계약 갱신 기한",
                    "정기 운용현황 보고", "DC형 신규 가입자 운용지시 안내", "퇴직연금 신고의무 이행",
                    "부담금 납입 기한", "수수료 납입 기한 도래", "퇴직급여 지급 처리",
                    "운용관리 수수료 정산", "DB형 적립금 운용 보고", "DC형 가입자 교육 실시",
                    "퇴직연금 규약 변경 신고", "적립금 운용현황 통지", "DB형 재정검증 보고",
                    "IRP 계좌 개설 안내", "퇴직급여 중간정산 처리", "운용상품 변경 안내",
                    "가입자 명부 정리", "연간 운용보고서 제출", "DC형 디폴트옵션 안내",
                    "부담금 미납 독촉", "퇴직연금 사업보고서 제출", "가입자 적립금 현황 통지",
                    "DB형 기금 재정 점검", "퇴직급여 사전 통지", "운용지시서 갱신",
                    "퇴직연금 교육 자료 배포", "연금계리 보고서 검토", "분기별 운용현황 보고"
            };

            String[] descriptions = {
                    "퇴직 가입자의 퇴직급여 정산 및 지급 처리가 필요합니다.",
                    "DC형 가입자의 운용지시 기한이 도래하였습니다. 운용지시를 확인하세요.",
                    "DB형 퇴직연금 계약 갱신 기한입니다. 계약 조건을 검토하세요.",
                    "정기 운용현황 보고서를 작성하여 제출해야 합니다.",
                    "신규 입사자 DC형 퇴직연금 운용지시 안내 기한입니다.",
                    "퇴직연금 운용 현황 고용노동부 신고 기한입니다.",
                    "부담금 납입 기한이 도래하였습니다. 납입 처리를 진행하세요.",
                    "퇴직연금 수수료 납입 기한이 도래하였습니다.",
                    "퇴직 가입자에 대한 퇴직급여 지급을 처리해야 합니다.",
                    "운용관리 수수료 정산 기한입니다.",
                    "DB형 적립금 운용 현황을 보고해야 합니다.",
                    "DC형 가입자 대상 퇴직연금 교육을 실시해야 합니다.",
                    "퇴직연금 규약 변경 사항을 고용노동부에 신고해야 합니다.",
                    "가입자별 적립금 운용현황을 통지해야 합니다.",
                    "DB형 퇴직연금 재정검증 결과를 보고해야 합니다.",
                    "신규 퇴직자에게 IRP 계좌 개설을 안내해야 합니다.",
                    "중간정산 요청 건에 대한 처리가 필요합니다.",
                    "운용상품 변경 사항을 가입자에게 안내해야 합니다.",
                    "가입자 명부를 최신 상태로 정리해야 합니다.",
                    "연간 운용보고서를 작성하여 제출해야 합니다.",
                    "DC형 디폴트옵션 적용 대상자에게 안내해야 합니다.",
                    "부담금 미납 기업에 대한 독촉 안내가 필요합니다.",
                    "퇴직연금 사업보고서를 제출해야 합니다.",
                    "가입자별 적립금 현황을 통지해야 합니다.",
                    "DB형 기금 재정 상태를 점검해야 합니다.",
                    "퇴직 예정자에게 퇴직급여를 사전 통지해야 합니다.",
                    "운용지시서를 갱신하여 제출해야 합니다.",
                    "퇴직연금 교육 자료를 배포해야 합니다.",
                    "연금계리 보고서를 검토해야 합니다.",
                    "분기별 운용현황 보고서를 작성하여 제출해야 합니다."
            };

            LocalDate today = LocalDate.now();

            for (int i = 0; i < 30; i++) {
                CompanyRetirement cr = allRetirements.get(i % allRetirements.size());

                LocalDate dueDate;
                ScheduleStatus status;

                if (i < 5) {
                    // 기한 초과 (과거)
                    dueDate = today.minusDays(3 + i * 5);
                    status = ScheduleStatus.OVERDUE;
                } else if (i < 10) {
                    // 완료
                    dueDate = today.minusDays(1 + i);
                    status = ScheduleStatus.DONE;
                } else if (i < 20) {
                    // 기일 임박 (2주 내)
                    dueDate = today.plusDays(1 + (i - 10));
                    status = ScheduleStatus.ACTIVE;
                } else {
                    // 향후 일정
                    dueDate = today.plusDays(15 + (i - 20) * 3);
                    status = ScheduleStatus.ACTIVE;
                }

                LocalDate createdDate = dueDate.minusDays(10 + i);

                // 일부 일정에 연관 가입자 추가
                List<Employee> targetEmps = new ArrayList<>();
                if (i % 3 == 0 && !employees.isEmpty()) {
                    int start = (i * 2) % employees.size();
                    for (int k = 0; k < Math.min(3, employees.size() - start); k++) {
                        targetEmps.add(employees.get(start + k));
                    }
                }

                scheduleRepository.save(
                        Schedule.builder()
                                .dueDate(dueDate)
                                .title(titles[i])
                                .description(descriptions[i])
                                .status(status)
                                .createdDate(createdDate)
                                .companyRetirement(cr)
                                .targetEmployees(targetEmps)
                                .build()
                );
            }

            System.out.println("=== Mock Data Insert Complete ===");
        };
    }
}
