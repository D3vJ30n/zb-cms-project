package com.zerobase.cms.user.controller;

import com.zerobase.cms.user.domain.dto.SignInForm;
import com.zerobase.cms.user.domain.dto.SignUpForm;
import com.zerobase.cms.user.domain.dto.response.ResponseDto;
import com.zerobase.cms.user.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/signup/seller")
    public ResponseEntity<ResponseDto<String>> signUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(
            ResponseDto.success("회원 가입 성공", sellerService.signUp(form))
        );
    }

    @GetMapping("/verify/seller")
    public ResponseEntity<ResponseDto<String>> verifyEmail(@RequestParam String email) {
        return ResponseEntity.ok(
            ResponseDto.success("인증 성공", sellerService.verifyEmail(email))
        );
    }

    @PostMapping("/signin/seller")
    public ResponseEntity<ResponseDto<String>> signIn(@RequestBody SignInForm form) {
        return ResponseEntity.ok(
            ResponseDto.success("로그인 성공", sellerService.signIn(form))
        );
    }
}
