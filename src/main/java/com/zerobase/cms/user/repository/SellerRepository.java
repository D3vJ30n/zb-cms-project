package com.zerobase.cms.user.repository;

import com.zerobase.cms.user.domain.seller.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    // 이메일로 판매자 찾기
    Optional<Seller> findByEmail(String email);
    
    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);
    
    Optional<Seller> findByIdAndEmail(Long id, String email);
}
