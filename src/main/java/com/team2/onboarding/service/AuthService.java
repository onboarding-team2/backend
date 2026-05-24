package com.team2.onboarding.service;

import com.team2.onboarding.dto.LoginRequestDto;
import com.team2.onboarding.entity.Company;
import com.team2.onboarding.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CompanyRepository companyRepository;

    public String login(LoginRequestDto request) {

        Company company = companyRepository.findByBrn(request.getBrn())
                .orElseThrow(() ->
                        new IllegalArgumentException("사업자번호가 존재하지 않습니다.")
                );

        if (!company.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        return "로그인 성공";
    }
}