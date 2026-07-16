package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.domain.Product;
import com.example.likelion14th_springboot.dto.request.ProductCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.ProductDeleteRequestDto;
import com.example.likelion14th_springboot.dto.request.ProductUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.ProductResponseDto;
import com.example.likelion14th_springboot.repository.MemberRepository;
import com.example.likelion14th_springboot.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ProductResponseDto createProduct(ProductCreateRequestDto dto) {

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 판매자입니다."));

        // 판매자 권한 확인
        if (!member.isSeller()) {
            throw new IllegalArgumentException("상품은 판매자만 등록할 수 있습니다.");
        }

        Product product = dto.toEntity(member); // dto => 실제 엔티티로 변환
        Product saved = productRepository.save(product); // 변환된 엔티티를 데이터베이스에 저장
        return new ProductResponseDto(saved.getId(), saved.getName(),
                saved.getPrice(), saved.getStock(), saved.getDescription()); // 응답
    }

    // 모든 상품 가져오기
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponseDto::fromEntity)
                .toList();
    }

    // 특정 상품 가져오기
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        return ProductResponseDto.fromEntity(product);
    }

    // 상품 업데이트
    @Transactional
    public ProductResponseDto updateProduct(Long productId, ProductUpdateRequestDto dto) {
        // 판매자 조회
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 판매자가 존재하지 않습니다."));

        // 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        // 판매자 권한 확인
        if (!product.getSeller().getId().equals(member.getId())) {
            throw new IllegalArgumentException("본인의 상품만 수정할 수 있습니다.");
        }

        // 상품 수정 수행
        product.update(dto.getName(), dto.getPrice(), dto.getStock(), dto.getDescription());

        return ProductResponseDto.fromEntity(product);
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long productId, ProductDeleteRequestDto dto) {
        // 판매자 조회
        Member seller = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));

        // 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 권한 확인
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new IllegalArgumentException("본인의 상품만 삭제할 수 있습니다.");
        }

        // 삭제 수행
        productRepository.delete(product);
    }

}