package com.example.likelion14th_springboot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.domain.Orders;
import com.example.likelion14th_springboot.domain.Product;
import com.example.likelion14th_springboot.domain.ShippingAddress;
import com.example.likelion14th_springboot.domain.mapping.ProductOrders;
import com.example.likelion14th_springboot.dto.request.OrdersCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.OrdersUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.OrdersResponseDto;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import com.example.likelion14th_springboot.enums.Role;
import com.example.likelion14th_springboot.repository.MemberRepository;
import com.example.likelion14th_springboot.repository.OrdersRepository;
import com.example.likelion14th_springboot.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @InjectMocks
    private OrdersService ordersService;

    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ProductRepository productRepository;

    @Test
    @DisplayName("주문 생성 성공: 재고와 잔액이 충분하면 재고/잔액이 차감되고 주문이 PREPARATION 상태로 생성된다.")
    void createOrder_Success() {
        // given
        Member buyer = Member.builder().name("중대멋사홍보팀").role(Role.BUYER).deposit(50000).build();
        ReflectionTestUtils.setField(buyer, "id", 1L);

        Product product = Product.builder().name("멋사 엠티 굿즈").price(10000).stock(10).build();
        ReflectionTestUtils.setField(product, "id", 100L);

        OrdersCreateRequestDto.OrderProductDto itemDto = OrdersCreateRequestDto.OrderProductDto.builder()
                .productId(100L).quantity(2).build();

        OrdersCreateRequestDto requestDto = OrdersCreateRequestDto.builder()
                .buyerId(1L)
                .recipient("중대멋사아기사자1").phoneNumber("010-1234-5678")
                .roadAddress("서울시 동작구").detailAddress("101호").postCode("06974")
                .products(List.of(itemDto))
                .build();

        Orders savedOrder = Orders.builder()
                .buyer(buyer)
                .deliverStatus(DeliverStatus.PREPARATION)
                .shippingAddress(ShippingAddress.builder().recipient("중대멋사아기사자1").build())
                .productOrders(List.of(ProductOrders.builder().product(product).quantity(2).build()))
                .build();
        ReflectionTestUtils.setField(savedOrder, "id", 10L);

        given(memberRepository.findById(1L)).willReturn(Optional.of(buyer));
        given(productRepository.findById(100L)).willReturn(Optional.of(product));
        given(ordersRepository.save(any(Orders.class))).willReturn(savedOrder);

        // when
        OrdersResponseDto result = ordersService.createOrder(requestDto);

        // then
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getDeliverStatus()).isEqualTo(DeliverStatus.PREPARATION);
        assertThat(product.getStock()).isEqualTo(8); // 10개 - 2개 차감 확인
        assertThat(buyer.getDeposit()).isEqualTo(30000); // 50000원 - (10000*2) 차감 확인
        verify(ordersRepository).save(any(Orders.class));
    }

    @Test
    @DisplayName("주문 생성 실패: 구매자의 잔액이 부족하면 예외가 발생한다.")
    void createOrder_Fail_Deposit() {
        // given
        Member buyer = Member.builder().name("빈곤한구매자").role(Role.BUYER).deposit(0).build();
        Product product = Product.builder().name("엄청엄청비싼상품").price(10000).stock(10).build();

        OrdersCreateRequestDto.OrderProductDto itemDto = OrdersCreateRequestDto.OrderProductDto.builder()
                .productId(100L).quantity(1).build();
        OrdersCreateRequestDto requestDto = OrdersCreateRequestDto.builder()
                .buyerId(1L).products(List.of(itemDto)).build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(buyer));
        given(productRepository.findById(100L)).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> ordersService.createOrder(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구매자의 계좌 잔액이 부족합니다.");
    }

    @Test
    @DisplayName("배송 정보 수정 성공: 배송 상태가 PREPARATION이면 주소가 정상 수정된다.")
    void updateShippingInfo_Success() {
        // given
        Orders order = Orders.builder()
                .deliverStatus(DeliverStatus.PREPARATION)
                .shippingAddress(ShippingAddress.builder().recipient("구수령인").build())
                .productOrders(List.of())
                .build();
        ReflectionTestUtils.setField(order, "id", 1L);

        OrdersUpdateRequestDto updateDto = OrdersUpdateRequestDto.builder()
                .recipient("중대멋사아기사자2").phoneNumber("010-9999-8888")
                .roadAddress("새 도로명").detailAddress("새 상세").postCode("12345")
                .build();

        given(ordersRepository.findById(1L)).willReturn(Optional.of(order));

        // when
        OrdersResponseDto result = ordersService.updateShippingInfo(1L, updateDto);

        // then
        assertThat(result.getShippingAddress().getRecipient()).isEqualTo("중대멋사아기사자2");
        assertThat(order.getShippingAddress().getRoadAddress()).isEqualTo("새 도로명");
    }

    @Test
    @DisplayName("배송 정보 수정 실패: 배송 상태가 DEPARTURE(출발)이면 예외가 발생한다.")
    void updateShippingInfo_Fail_Status() {
        // given
        Orders order = Orders.builder()
                .deliverStatus(DeliverStatus.DEPARTURE) // 배송출발 상태
                .build();
        given(ordersRepository.findById(1L)).willReturn(Optional.of(order));

        OrdersUpdateRequestDto updateDto = OrdersUpdateRequestDto.builder().recipient("변경시도").build();

        // when & then
        assertThatThrownBy(() -> ordersService.updateShippingInfo(1L, updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("배송 준비 중(PREPARATION) 상태에서만 배송 정보를 수정할 수 있습니다.");
    }

    @Test
    @DisplayName("주문 삭제 성공: 배송 완료(COMPLETED) 상태에서만 삭제가 수행된다.")
    void softDeleteOrder_Success() {
        // given
        Orders order = Orders.builder()
                .deliverStatus(DeliverStatus.COMPLETED)
                .build();
        given(ordersRepository.findById(1L)).willReturn(Optional.of(order));

        // when
        ordersService.softDeleteOrder(1L);

        // then
        verify(ordersRepository).delete(order);
    }
}