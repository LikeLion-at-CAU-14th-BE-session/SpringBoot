package com.example.likelion14th_springboot.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor // 테스트 및 ObjectMapper 매핑 편의를 위해 전체 생성자/기본 생성자 세트로 적었음 - 세션에선 getter만 적음!
public class OrdersCreateRequestDto {

    private Long buyerId;         // 구매자 ID
    private String recipient;     // 수령인 이름
    private String phoneNumber;   // 수령인 전화번호
    private String roadAddress;   // 도로명 주소
    private String detailAddress; // 상세 주소
    private String postCode;      // 우편번호

    private List<OrderProductDto> products; // 주문할 상품 목록

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderProductDto {
        private Long productId;   // 상품 ID
        private Integer quantity; // 주문 수량
    }
}