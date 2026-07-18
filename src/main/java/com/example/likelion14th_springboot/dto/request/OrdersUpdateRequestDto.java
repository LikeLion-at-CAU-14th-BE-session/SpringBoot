package com.example.likelion14th_springboot.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdersUpdateRequestDto {

    private String recipient;     // 수령인 이름
    private String phoneNumber;   // 전화번호
    private String roadAddress;   // 도로명 주소
    private String detailAddress; // 상세 주소
    private String postCode;      // 우편번호
}