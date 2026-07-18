package com.example.likelion14th_springboot.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// @Embeddable: 별도의 테이블을 생성하지 않고, 부모 엔티티(Orders)의 테이블 컬럼으로 합쳐서 매핑
@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {

    private String recipient;     // 수령인 이름
    private String phoneNumber;   // 전화번호
    private String roadAddress;   // 도로명 주소
    private String detailAddress; // 상세 주소
    private String postCode;      // 우편번호
}