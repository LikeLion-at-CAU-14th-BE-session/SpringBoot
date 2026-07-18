package com.example.likelion14th_springboot.repository;

import com.example.likelion14th_springboot.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
