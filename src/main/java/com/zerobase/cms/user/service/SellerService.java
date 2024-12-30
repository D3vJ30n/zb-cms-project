package com.zerobase.cms.user.service;

import com.zerobase.cms.user.config.JwtAuthenticationProvider;
import com.zerobase.cms.user.config.MailgunConfig;
import com.zerobase.cms.user.domain.Seller;
import com.zerobase.cms.user.domain.dto.SignInForm;
import com.zerobase.cms.user.domain.dto.SignUpForm;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailgunConfig mailgunConfig;
    private final JwtAuthenticationProvider provider;

    @Transactional
    public String signUp(SignUpForm form) {
        if (sellerRepository.existsByEmail(form.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
        }

        Seller seller = Seller.builder()
            .email(form.getEmail().toLowerCase(Locale.ROOT))
            .name(form.getName())
            .password(passwordEncoder.encode(form.getPassword()))
            .phone(form.getPhone())
            .verified(false)
            .build();

        sellerRepository.save(seller);

        String verificationText = "다음 링크를 클릭하여 이메일을 인증해주세요: " +
            "http://localhost:8080/verify/seller?email=" + seller.getEmail();
        
        boolean mailResult = mailgunConfig.sendEmail(
            seller.getEmail(),
            "판매자 이메일 인증을 완료해주세요.",
            verificationText
        );

        if (!mailResult) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
        }

        return "회원 가입이 완료되었습니다. 이메일을 확인해주세요.";
    }

    @Transactional
    public String verifyEmail(String email) {
        Seller seller = sellerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (seller.isVerified()) {
            throw new CustomException(ErrorCode.ALREADY_VERIFIED);
        }

        seller.setVerified(true);
        sellerRepository.save(seller);

        return "이메일 인증이 완료되었습니다.";
    }

    @Transactional
    public String signIn(SignInForm form) {
        Seller seller = sellerRepository.findByEmail(form.getEmail().toLowerCase(Locale.ROOT))
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(form.getPassword(), seller.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }

        if (!seller.isVerified()) {
            throw new CustomException(ErrorCode.NOT_VERIFIED_EMAIL);
        }

        return provider.createToken(seller.getEmail(), seller.getId());
    }
}
