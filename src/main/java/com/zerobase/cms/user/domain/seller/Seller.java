package com.zerobase.cms.user.domain.seller;

import com.zerobase.cms.user.domain.BaseEntity;
import com.zerobase.cms.user.domain.dto.SignUpForm;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;

@Entity(name = "SellerEntity")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "seller")
public class Seller extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String email;
    private String name;
    private String password;
    private String phone;

    private String verificationCode;
    private boolean verify;

    public static Seller from(SignUpForm form) {
        return Seller.builder()
                .email(form.getEmail())
                .name(form.getName())
                .password(form.getPassword())
                .phone(form.getPhone())
                .verify(false)
                .build();
    }
}
