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

import com.example.likelion14th_springboot.dto.request.ProductCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.ProductDeleteRequestDto;
import com.example.likelion14th_springboot.dto.request.ProductUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.ProductResponseDto;
import com.example.likelion14th_springboot.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    // @WebMvcTest에서 @EnableJpaAuditing(BaseTimeEntity) 에러가 발생하지 않도록 가짜 JPA 메타모델 주입
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("POST /products : 상품 등록 API")
    void createProduct() throws Exception {
        // given
        ProductCreateRequestDto requestDto = new ProductCreateRequestDto();
        ReflectionTestUtils.setField(requestDto, "name", "멋사 후드티");
        ReflectionTestUtils.setField(requestDto, "price", 35000);
        ReflectionTestUtils.setField(requestDto, "stock", 30);
        ReflectionTestUtils.setField(requestDto, "description", "겨울 굿즈");
        ReflectionTestUtils.setField(requestDto, "memberId", 1L);

        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(10L).name("멋사 후드티").price(35000).stock(30).description("겨울 굿즈").build();

        given(productService.createProduct(any(ProductCreateRequestDto.class))).willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("멋사 후드티"))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /products : 전체 상품 목록 조회 API")
    void getAllProducts() throws Exception {
        // given
        ProductResponseDto res1 = ProductResponseDto.builder().id(1L).name("상품1").price(1000).stock(10)
                .description("설명1").build();
        ProductResponseDto res2 = ProductResponseDto.builder().id(2L).name("상품2").price(2000).stock(20)
                .description("설명2").build();

        given(productService.getAllProducts()).willReturn(List.of(res1, res2));

        // when & then
        mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("상품1"))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /products/{id} : 특정 상품 조회 API")
    void getProductById() throws Exception {
        // given
        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(1L).name("단일상품").price(5000).stock(15).description("단일설명").build();

        given(productService.getProductById(1L)).willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/products/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("단일상품"))
                .andExpect(jsonPath("$.price").value(5000))
                .andDo(print());
    }

    @Test
    @DisplayName("PUT /products/{id} : 상품 수정 API")
    void updateProduct() throws Exception {
        // given
        ProductUpdateRequestDto requestDto = new ProductUpdateRequestDto();
        ReflectionTestUtils.setField(requestDto, "name", "수정된 상품");
        ReflectionTestUtils.setField(requestDto, "price", 9999);
        ReflectionTestUtils.setField(requestDto, "stock", 5);
        ReflectionTestUtils.setField(requestDto, "description", "수정된 설명");
        ReflectionTestUtils.setField(requestDto, "memberId", 1L);

        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(1L).name("수정된 상품").price(9999).stock(5).description("수정된 설명").build();

        given(productService.updateProduct(eq(1L), any(ProductUpdateRequestDto.class))).willReturn(responseDto);

        // when & then
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정된 상품"))
                .andDo(print());
    }

    @Test
    @DisplayName("DELETE /products/{id} : 상품 삭제 API")
    void deleteProduct() throws Exception {
        // given
        ProductDeleteRequestDto requestDto = new ProductDeleteRequestDto();
        ReflectionTestUtils.setField(requestDto, "memberId", 1L);

        // when & then
        mockMvc.perform(delete("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("상품이 성공적으로 삭제되었습니다."))
                .andDo(print());

        verify(productService).deleteProduct(eq(1L), any(ProductDeleteRequestDto.class));
    }
}