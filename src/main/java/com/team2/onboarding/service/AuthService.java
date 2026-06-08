package com.team2.onboarding.service;

import com.team2.onboarding.dto.FindPasswordRequestDto;
import com.team2.onboarding.dto.LoginRequestDto;
import com.team2.onboarding.dto.LoginResponseDto;
import com.team2.onboarding.dto.ResetPasswordRequestDto;
import com.team2.onboarding.entity.Company;
import com.team2.onboarding.entity.CompanyRetirementDc;
import com.team2.onboarding.enums.PlanType;
import com.team2.onboarding.repository.CompanyRepository;
import com.team2.onboarding.repository.CompanyRetirementDcRepository;
import com.team2.onboarding.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CompanyRepository companyRepository;
    private final CompanyRetirementDcRepository companyRetirementDcRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponseDto login(LoginRequestDto request) {
        Company company = companyRepository.findByBrn(request.getBrn())
                .orElseThrow(() -> new IllegalArgumentException("사업자번호가 존재하지 않습니다."));

        if (company.getPassword() == null) {
            throw new IllegalArgumentException("비밀번호가 초기화되지 않았습니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), company.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        PlanType planType = companyRetirementDcRepository
                .findByCompanyId(company.getId())
                .map(CompanyRetirementDc::getPlanType)
                .orElse(PlanType.DC);

        String token = jwtTokenProvider.createToken(company.getId().toString());
        return new LoginResponseDto(token, planType);
    }

    public String verifyCompanyForPasswordReset(FindPasswordRequestDto request) {
        companyRepository.findByCompanyNameAndBrn(request.getCompanyName(), request.getBrn())
                .orElseThrow(() -> new IllegalArgumentException("입력하신 기업 정보와 일치하는 계정이 없습니다."));

        return "기업 확인 완료. 비밀번호 재설정이 가능합니다.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequestDto request) {
        Company company = companyRepository.findByCompanyNameAndBrn(request.getCompanyName(), request.getBrn())
                .orElseThrow(() -> new IllegalArgumentException("기업 정보가 올바르지 않습니다."));

        String encryptedPassword = passwordEncoder.encode(request.getNewPassword());
        company.updatePassword(encryptedPassword);

        return "비밀번호 재설정이 완료되었습니다.";
    }
}
