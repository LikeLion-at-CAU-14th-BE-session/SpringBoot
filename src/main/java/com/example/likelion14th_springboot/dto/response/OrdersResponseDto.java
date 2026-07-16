package com.example.likelion14th_springboot.dto.response;

import com.example.likelion14th_springboot.domain.Orders;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrdersResponseDto {

    private Long id;                     // 주문 ID
    private DeliverStatus deliverStatus; // 배송 상태
    private ShippingAddressDto shippingAddress; // 배송 정보
    private List<ProductInfo> products;  // 주문 상품 정보 목록

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ShippingAddressDto {
        private String recipient;
        private String phoneNumber;
        private String roadAddress;
        private String detailAddress;
        private String postCode;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProductInfo {
        private Long productId; // 상품 ID
        private String name;    // 상품명
        private Integer price;  // 상품 가격
        private Integer quantity; // 주문 수량
    }

    // Entity -> DTO 변환 정적 팩토리 메서드
    public static OrdersResponseDto fromEntity(Orders order) {
        return OrdersResponseDto.builder()
                .id(order.getId())
                .deliverStatus(order.getDeliverStatus())
                .shippingAddress(ShippingAddressDto.builder()
                        .recipient(order.getShippingAddress().getRecipient())
                        .phoneNumber(order.getShippingAddress().getPhoneNumber())
                        .roadAddress(order.getShippingAddress().getRoadAddress())
                        .detailAddress(order.getShippingAddress().getDetailAddress())
                        .postCode(order.getShippingAddress().getPostCode())
                        .build())
                .products(order.getProductOrders().stream()
                        .map(po -> ProductInfo.builder()
                                .productId(po.getProduct().getId())
                                .name(po.getProduct().getName())
                                .price(po.getProduct().getPrice())
                                .quantity(po.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}