package com.team2.onboarding.controller;

import com.team2.onboarding.dto.EmployeeDetailResponseDto;
import com.team2.onboarding.dto.EmployeeListResponseDto;
import com.team2.onboarding.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 가입자 관리 API.
 *
 * TODO(JWT): 지금은 companyId 를 쿼리 파라미터로 받는다.
 *            JWT 검증 인터셉터가 들어오면 SecurityContext 에서 꺼내도록 교체.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    /** 가입자 명부 조회 + 검색 + 페이징. */
    @GetMapping
    public ResponseEntity<EmployeeListResponseDto> getEmployees(
            @RequestParam String companyId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                employeeService.getEmployees(companyId, name, status, page, size)
        );
    }

    /** 가입자 상세 조회. */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDetailResponseDto> getEmployeeDetail(
            @PathVariable Long id,
            @RequestParam String companyId
    ) {
        return ResponseEntity.ok(
                employeeService.getEmployeeDetail(companyId, id)
        );
    }
}
