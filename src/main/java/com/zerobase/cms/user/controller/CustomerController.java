package com.zerobase.cms.user.controller;

import com.zerobase.cms.user.domain.dto.SignInForm;
import com.zerobase.cms.user.domain.dto.SignUpForm;
import com.zerobase.cms.user.domain.dto.response.ResponseDto;
import com.zerobase.cms.user.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/signup/customer")
    public ResponseEntity<ResponseDto<String>> signUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(
            ResponseDto.success("회원 가입 성공", customerService.signUp(form))
        );
    }

    @GetMapping("/verify/customer")
    public ResponseEntity<ResponseDto<String>> verifyEmail(@RequestParam String email) {
        return ResponseEntity.ok(
            ResponseDto.success("인증 성공", customerService.verifyEmail(email))
        );
    }

    @PostMapping("/signin/customer")
    public ResponseEntity<ResponseDto<String>> signIn(@RequestBody SignInForm form) {
        return ResponseEntity.ok(
            ResponseDto.success("로그인 성공", customerService.signIn(form))
        );
    }

    @PutMapping("/customer/balance")
    public ResponseEntity<ResponseDto<String>> changeBalance(
        @RequestParam Long customerId,
        @RequestParam Integer amount) {
        return ResponseEntity.ok(
            ResponseDto.success("잔액 변경 성공", 
                customerService.changeBalance(customerId, amount))
        );
    }
}
