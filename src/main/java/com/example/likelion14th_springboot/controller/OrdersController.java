package com.example.likelion14th_springboot.controller;

import com.example.likelion14th_springboot.dto.request.OrdersCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.OrdersUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.OrdersResponseDto;
import com.example.likelion14th_springboot.service.OrdersService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrdersController {

    private final OrdersService ordersService;

    // 1. 주문 생성
    @PostMapping
    public ResponseEntity<OrdersResponseDto> createOrder(@RequestBody OrdersCreateRequestDto dto) {
        return ResponseEntity.ok(ordersService.createOrder(dto));
    }

    // 2-1. 구매자별 주문 목록 조회
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<OrdersResponseDto>> getOrdersByBuyer(@PathVariable Long buyerId) {
        return ResponseEntity.ok(ordersService.getOrdersByBuyer(buyerId));
    }

    // 2-2. 단건 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrdersResponseDto> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(ordersService.getOrderDetail(orderId));
    }

    // 3. 배송정보 수정
    @PutMapping("/{orderId}/address")
    public ResponseEntity<OrdersResponseDto> updateShippingInfo(@PathVariable Long orderId,
                                                                @RequestBody OrdersUpdateRequestDto dto) {
        return ResponseEntity.ok(ordersService.updateShippingInfo(orderId, dto));
    }

    // 4. 주문 삭제 (Soft Delete)
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        ordersService.softDeleteOrder(orderId);
        return ResponseEntity.ok("주문이 성공적으로 삭제되었습니다.");
    }
}