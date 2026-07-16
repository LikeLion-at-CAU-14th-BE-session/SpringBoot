package com.example.likelion14th_springboot.domain;

import com.example.likelion14th_springboot.domain.mapping.ProductOrders;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ?")
public class Orders extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DeliverStatus deliverStatus; // 배송상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private Member buyer;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<ProductOrders> productOrders;

    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    private Coupon coupon;

    // 배송 정보 임베디드 매핑
    @Embedded
    private ShippingAddress shippingAddress;

    // Soft Delete 여부 플래그 (기본값 false)
    @Builder.Default
    private Boolean deleted = false;

    // 배송 정보 수정용 비즈니스 메서드
    public void updateShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
