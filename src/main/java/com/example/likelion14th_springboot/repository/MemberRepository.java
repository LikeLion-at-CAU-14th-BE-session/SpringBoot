package com.example.likelion14th_springboot.repository;

import com.example.likelion14th_springboot.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    // 1. 나이 기준 조회 및 페이징 (정렬은 Pageable 객체에서 처리)
    Page<Member> findByAgeGreaterThanEqual(Integer age, Pageable pageable);

    // 2. 이름 시작 값으로 필터링
    List<Member> findByNameStartingWith(String prefix);
}