package com.example.likelion14th_springboot.repository;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.domain.Orders;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    // 구매자(Member)별 주문 목록 조회
    List<Orders> findByBuyer(Member buyer);
}