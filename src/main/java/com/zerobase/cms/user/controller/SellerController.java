package com.zerobase.cms.user.controller;

import com.zerobase.cms.user.domain.dto.SignInForm;
import com.zerobase.cms.user.domain.dto.SignUpForm;
import com.zerobase.cms.user.domain.dto.response.ResponseDto;
import com.zerobase.cms.user.domain.seller.Seller;
import com.zerobase.cms.user.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<Seller>> signUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(
            ResponseDto.success("판매자 회원가입 성공", sellerService.signUp(form))
        );
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseDto<String>> signIn(@RequestBody SignInForm form) {
        return ResponseEntity.ok(
            ResponseDto.success("로그인 성공", sellerService.signIn(form))
        );
    }

    @GetMapping("/verify")
    public ResponseEntity<ResponseDto<String>> verify(@RequestParam String email) {
        sellerService.verifyEmail(email);
        return ResponseEntity.ok(
            ResponseDto.success("인증이 완료되었습니다.")
        );
    }
}
