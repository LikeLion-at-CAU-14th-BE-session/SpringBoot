package com.example.likelion14th_springboot.dto.request;

import lombok.Getter;

@Getter
public class ProductUpdateRequestDto {
    private String name;
    private Integer price;
    private Integer stock;
    private String description;
    private Long memberId;
}