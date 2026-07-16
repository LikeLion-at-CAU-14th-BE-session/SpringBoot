package com.example.likelion14th_springboot.dto.request;


import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.domain.Product;
import lombok.Getter;

@Getter
public class ProductCreateRequestDto {
    private String name;
    private Integer price;
    private Integer stock;
    private String description;
    private Long memberId; // 판매자인지 확인용

    public Product toEntity(Member seller) {
        return Product.builder()
                .name(this.name)
                .price(this.price)
                .stock(this.stock)
                .description(this.description)
                .seller(seller)
                .build();
    }
}