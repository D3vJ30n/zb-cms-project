package com.zerobase.cms.user.service;

import com.zerobase.cms.user.config.JwtAuthenticationProvider;
import com.zerobase.cms.user.config.MailgunConfig;
import com.zerobase.cms.user.domain.Customer;
import com.zerobase.cms.user.domain.dto.SignInForm;
import com.zerobase.cms.user.domain.dto.SignUpForm;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailgunConfig mailgunConfig;
    private final JwtAuthenticationProvider provider;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        return User.builder()
            .username(customer.getEmail())
            .password(customer.getPassword())
            .roles("USER")
            .build();
    }

    @Transactional
    public String signUp(SignUpForm form) {
        if (customerRepository.existsByEmail(form.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
        }

        Customer customer = Customer.builder()
            .email(form.getEmail().toLowerCase(Locale.ROOT))
            .name(form.getName())
            .password(passwordEncoder.encode(form.getPassword()))
            .phone(form.getPhone())
            .verified(false)
            .balance(0)
            .build();

        customerRepository.save(customer);

        String verificationText = "다음 링크를 클릭하여 이메일을 인증해주세요: " +
            "http://localhost:8080/verify/customer?email=" + customer.getEmail();
        
        boolean mailResult = mailgunConfig.sendEmail(
            customer.getEmail(),
            "이메일 인증을 완료해주세요.",
            verificationText
        );

        if (!mailResult) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
        }

        return "회원 가입이 완료되었습니다. 이메일을 확인해주세요.";
    }

    @Transactional
    public String verifyEmail(String email) {
        Customer customer = customerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (customer.isVerified()) {
            throw new CustomException(ErrorCode.ALREADY_VERIFIED);
        }

        customer.setVerified(true);
        customerRepository.save(customer);

        return "이메일 인증이 완료되었습니다.";
    }

    @Transactional
    public String signIn(SignInForm form) {
        Customer customer = customerRepository.findByEmail(form.getEmail().toLowerCase(Locale.ROOT))
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(form.getPassword(), customer.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }

        if (!customer.isVerified()) {
            throw new CustomException(ErrorCode.NOT_VERIFIED_EMAIL);
        }

        return provider.createToken(customer.getEmail(), customer.getId());
    }

    @Transactional
    public String changeBalance(Long customerId, Integer amount) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        customer.setBalance(customer.getBalance() + amount);
        customerRepository.save(customer);

        return customer.getName() + "님의 잔액이 " + amount + "원 변경되었습니다. " +
            "현재 잔액: " + customer.getBalance() + "원";
    }
}
