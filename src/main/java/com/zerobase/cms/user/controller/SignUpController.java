package com.zerobase.cms.user.controller;

import com.zerobase.cms.user.application.SignUpApplication;
import com.zerobase.cms.user.domain.customer.Customer;
import com.zerobase.cms.user.domain.dto.SignUpForm;
import com.zerobase.cms.user.domain.dto.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpApplication signUpApplication;

    @PostMapping("/customer")
    public ResponseEntity<ResponseDto<Customer>> customerSignUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(
            ResponseDto.success("고객 회원가입 성공", signUpApplication.customerSignUp(form))
        );
    }

    @GetMapping("/customer/verify")
    public ResponseEntity<ResponseDto<String>> verifyCustomer(@RequestParam String email) {
        signUpApplication.verifyEmail(email);
        return ResponseEntity.ok(
            ResponseDto.success("인증이 완료되었습니다.")
        );
    }
}
