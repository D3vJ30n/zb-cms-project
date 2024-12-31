package com.zerobase.cms.user.service;

import com.zerobase.cms.user.config.JwtAuthenticationProvider;
import com.zerobase.cms.user.domain.seller.Seller;
import com.zerobase.cms.user.domain.dto.SignInForm;
import com.zerobase.cms.user.domain.dto.SignUpForm;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationProvider provider;

    public Optional<Seller> findByIdAndEmail(Long id, String email) {
        return sellerRepository.findByIdAndEmail(id, email);
    }

    public boolean isEmailExist(String email) {
        return sellerRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public Seller signUp(SignUpForm form) {
        if (sellerRepository.findByEmail(form.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
        }

        Seller seller = Seller.from(form);
        seller.setPassword(passwordEncoder.encode(form.getPassword()));
        seller.setVerify(false);

        return sellerRepository.save(seller);
    }

    @Transactional
    public void verifyEmail(String email) {
        Seller seller = sellerRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (seller.isVerify()) {
            throw new CustomException(ErrorCode.ALREADY_VERIFIED);
        }
        seller.setVerify(true);
    }

    public String signIn(SignInForm form) {
        Seller seller = sellerRepository.findByEmail(form.getEmail())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(form.getPassword(), seller.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }
        if (!seller.isVerify()) {
            throw new CustomException(ErrorCode.NOT_VERIFIED);
        }

        return provider.createToken(seller.getEmail(), seller.getId());
    }
}
