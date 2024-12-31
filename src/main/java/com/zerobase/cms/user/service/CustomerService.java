package com.zerobase.cms.user.service;

import com.zerobase.cms.user.config.JwtAuthenticationProvider;
import com.zerobase.cms.user.config.MailgunConfig;
import com.zerobase.cms.user.domain.customer.Customer;
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

import java.util.Locale;

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

    public boolean isEmailExist(String email) {
        return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
            .isPresent();
    }

    @Transactional
    public Customer signUp(SignUpForm form) {
        if (customerRepository.findByEmail(form.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
        }

        Customer customer = Customer.from(form);
        customer.setPassword(passwordEncoder.encode(form.getPassword()));
        customer.setVerify(false);

        return customerRepository.save(customer);
    }

    @Transactional
    public void verifyEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (customer.isVerify()) {
            throw new CustomException(ErrorCode.ALREADY_VERIFIED);
        }
        customer.setVerify(true);
    }

    public String signIn(SignInForm form) {
        Customer customer = customerRepository.findByEmail(form.getEmail())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(form.getPassword(), customer.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }
        if (!customer.isVerify()) {
            throw new CustomException(ErrorCode.NOT_VERIFIED);
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
