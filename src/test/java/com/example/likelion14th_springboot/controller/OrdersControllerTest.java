package com.example.likelion14th_springboot.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.likelion14th_springboot.dto.request.OrdersCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.OrdersUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.OrdersResponseDto;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import com.example.likelion14th_springboot.service.OrdersService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(OrdersController.class)
class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrdersService ordersService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext; // JPA Auditing 에러 방지용

    @Test
    @DisplayName("POST /orders : 주문 생성 API")
    void createOrder() throws Exception {
        // given
        OrdersCreateRequestDto.OrderProductDto itemDto = OrdersCreateRequestDto.OrderProductDto.builder()
                .productId(100L).quantity(2).build();
        OrdersCreateRequestDto requestDto = OrdersCreateRequestDto.builder()
                .buyerId(1L).recipient("중대멋사아기사자").products(List.of(itemDto)).build();

        OrdersResponseDto responseDto = OrdersResponseDto.builder()
                .id(1L).deliverStatus(DeliverStatus.PREPARATION).build();

        given(ordersService.createOrder(any(OrdersCreateRequestDto.class))).willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.deliverStatus").value("PREPARATION"))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /orders/buyer/{buyerId} : 구매자별 주문 목록 조회 API")
    void getOrdersByBuyer() throws Exception {
        // given
        OrdersResponseDto res1 = OrdersResponseDto.builder().id(1L).deliverStatus(DeliverStatus.PREPARATION).build();
        OrdersResponseDto res2 = OrdersResponseDto.builder().id(2L).deliverStatus(DeliverStatus.DEPARTURE).build();

        given(ordersService.getOrdersByBuyer(1L)).willReturn(List.of(res1, res2));

        // when & then
        mockMvc.perform(get("/orders/buyer/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[1].deliverStatus").value("DEPARTURE")) // DEPARTURE 검증
                .andDo(print());
    }

    @Test
    @DisplayName("PUT /orders/{orderId}/address : 배송 정보 수정 API")
    void updateShippingInfo() throws Exception {
        // given
        OrdersUpdateRequestDto requestDto = OrdersUpdateRequestDto.builder()
                .recipient("중대멋사으른사자").roadAddress("새 주소").build();

        OrdersResponseDto responseDto = OrdersResponseDto.builder()
                .id(1L).deliverStatus(DeliverStatus.PREPARATION)
                .shippingAddress(OrdersResponseDto.ShippingAddressDto.builder().recipient("중대멋사으른사자").build())
                .build();

        given(ordersService.updateShippingInfo(eq(1L), any(OrdersUpdateRequestDto.class))).willReturn(responseDto);

        // when & then
        mockMvc.perform(put("/orders/1/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingAddress.recipient").value("중대멋사으른사자"))
                .andDo(print());
    }

    @Test
    @DisplayName("DELETE /orders/{orderId} : 주문 삭제 API")
    void deleteOrder() throws Exception {
        // when & then
        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("주문이 성공적으로 삭제되었습니다."))
                .andDo(print());

        verify(ordersService).softDeleteOrder(1L);
    }
}