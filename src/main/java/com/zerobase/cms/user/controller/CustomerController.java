package com.zerobase.cms.user.controller;

import com.zerobase.cms.user.domain.customer.Customer;
import com.zerobase.cms.user.domain.dto.SignInForm;
import com.zerobase.cms.user.domain.dto.SignUpForm;
import com.zerobase.cms.user.domain.dto.response.ResponseDto;
import com.zerobase.cms.user.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto<Customer>> signUp(@RequestBody SignUpForm form) {
        Customer customer = customerService.signUp(form);
        return ResponseEntity.ok(ResponseDto.success("회원 가입 성공", customer));
    }

    @GetMapping("/verify")
    public ResponseEntity<ResponseDto<Void>> verifyEmail(@RequestParam String email) {
        customerService.verifyEmail(email);
        return ResponseEntity.ok(ResponseDto.success("인증 성공"));
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseDto<String>> signIn(@RequestBody SignInForm form) {
        String token = customerService.signIn(form);
        return ResponseEntity.ok(ResponseDto.success("로그인 성공", token));
    }

    @PutMapping("/balance")
    public ResponseEntity<ResponseDto<String>> changeBalance(
        @RequestParam Long customerId,
        @RequestParam Integer amount) {
        String result = customerService.changeBalance(customerId, amount);
        return ResponseEntity.ok(ResponseDto.success("잔액 변경 성공", result));
    }
}
