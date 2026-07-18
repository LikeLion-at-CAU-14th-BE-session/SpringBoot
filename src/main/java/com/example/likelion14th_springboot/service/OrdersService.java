package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.domain.Orders;
import com.example.likelion14th_springboot.domain.Product;
import com.example.likelion14th_springboot.domain.ShippingAddress;
import com.example.likelion14th_springboot.domain.mapping.ProductOrders;
import com.example.likelion14th_springboot.dto.request.OrdersCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.OrdersUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.OrdersResponseDto;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import com.example.likelion14th_springboot.repository.MemberRepository;
import com.example.likelion14th_springboot.repository.OrdersRepository;
import com.example.likelion14th_springboot.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    // 1. 주문 생성
    @Transactional
    public OrdersResponseDto createOrder(OrdersCreateRequestDto dto) {
        Member buyer = memberRepository.findById(dto.getBuyerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 구매자입니다."));

        ShippingAddress address = ShippingAddress.builder()
                .recipient(dto.getRecipient())
                .phoneNumber(dto.getPhoneNumber())
                .roadAddress(dto.getRoadAddress())
                .detailAddress(dto.getDetailAddress())
                .postCode(dto.getPostCode())
                .build();

        // 상품 검증, 재고 차감 및 ProductOrders 생성
        List<ProductOrders> productOrdersList = dto.getProducts().stream()
                .map(orderProductDto -> {
                    Product product = productRepository.findById(orderProductDto.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

                    if (product.getStock() < orderProductDto.getQuantity()) {
                        throw new IllegalArgumentException("상품의 재고가 부족합니다: " + product.getName());
                    }

                    product.reduceStock(orderProductDto.getQuantity()); // 재고 차감

                    return ProductOrders.builder()
                            .product(product)
                            .quantity(orderProductDto.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        // 총 주문 금액 계산
        int totalPrice = productOrdersList.stream()
                .mapToInt(po -> po.getProduct().getPrice() * po.getQuantity())
                .sum();

        // 구매자 계좌 잔액 확인 및 차감
        if (buyer.getDeposit() < totalPrice) {
            throw new IllegalArgumentException("구매자의 계좌 잔액이 부족합니다.");
        }
        buyer.useDeposit(totalPrice); // 잔액 차감

        // 주문 엔티티 생성 (초기 상태: PREPARATION)
        Orders order = Orders.builder()
                .buyer(buyer)
                .deliverStatus(DeliverStatus.PREPARATION)
                .shippingAddress(address)
                .productOrders(productOrdersList)
                .build();

        // 연관관계 연결
        productOrdersList.forEach(po -> po.setOrders(order));

        Orders savedOrder = ordersRepository.save(order);
        return OrdersResponseDto.fromEntity(savedOrder);
    }

    // 2-1. 구매자별 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrdersResponseDto> getOrdersByBuyer(Long buyerId) {
        Member buyer = memberRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 구매자입니다."));

        return ordersRepository.findByBuyer(buyer).stream()
                .map(OrdersResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 2-2. 단건 주문 상세 조회
    @Transactional(readOnly = true)
    public OrdersResponseDto getOrderDetail(Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        return OrdersResponseDto.fromEntity(order);
    }

    // 3. 배송 정보 수정 (PREPARATION 상태일 때만 가능)
    @Transactional
    public OrdersResponseDto updateShippingInfo(Long orderId, OrdersUpdateRequestDto dto) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (order.getDeliverStatus() != DeliverStatus.PREPARATION) {
            throw new IllegalArgumentException("배송 준비 중(PREPARATION) 상태에서만 배송 정보를 수정할 수 있습니다.");
        }

        ShippingAddress newAddress = ShippingAddress.builder()
                .recipient(dto.getRecipient())
                .phoneNumber(dto.getPhoneNumber())
                .roadAddress(dto.getRoadAddress())
                .detailAddress(dto.getDetailAddress())
                .postCode(dto.getPostCode())
                .build();

        order.updateShippingAddress(newAddress);
        return OrdersResponseDto.fromEntity(order);
    }

    // 4. 주문 삭제 (COMPLETED 상태일 때만 Soft Delete 가능)
    @Transactional
    public void softDeleteOrder(Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (order.getDeliverStatus() != DeliverStatus.COMPLETED) {
            throw new IllegalArgumentException("배송 완료(COMPLETED) 상태에서만 주문을 삭제할 수 있습니다.");
        }

        // @SQLDelete에 의해 deleted = true로 UPDATE 됨
        ordersRepository.delete(order);
    }
}