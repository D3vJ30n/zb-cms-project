package com.zerobase.cms.user.repository;

import com.zerobase.cms.user.domain.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // 이메일로 회원 찾기 (로그인, 중복 체크 등에 사용)
    Optional<Customer> findByEmail(String email);
    
    // 이메일 존재 여부 확인 (회원가입 시 중복 체크)
    boolean existsByEmail(String email);
}
