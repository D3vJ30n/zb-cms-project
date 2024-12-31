package com.zerobase.cms.user.application;

import com.zerobase.cms.user.domain.customer.Customer;
import com.zerobase.cms.user.domain.dto.SignUpForm;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpApplication {
    private final CustomerService customerService;

    public Customer customerSignUp(SignUpForm form) {
        if (customerService.isEmailExist(form.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
        }
        return customerService.signUp(form);
    }

    public void verifyEmail(String email) {
        customerService.verifyEmail(email);
    }
}
