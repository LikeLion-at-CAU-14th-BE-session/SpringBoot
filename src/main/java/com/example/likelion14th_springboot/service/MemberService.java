package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.dto.request.JoinRequestDto;
import com.example.likelion14th_springboot.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public List<Member> getAllMembers() {
        List<Member> memberList = memberRepository.findAll();
        return memberList;
    }

    public Member getByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        return member;
    }

    public Page<Member> getMembersByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Member> members = memberRepository.findAll(pageable);
        return members;
    }

    // 과제 1: 나이가 20 이상, 이름 기준 오름차순 정렬된 페이징 결과
    public Page<Member> getAdultMembersSortedByName(int page, int size) {
        // 이름 기준 오름차순 정렬 조건을 포함한 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return memberRepository.findByAgeGreaterThanEqual(20, pageable);
    }

    // 과제 2: 이름이 주어진 값으로 시작하는 경우 필터링
    public List<Member> getMembersByNamePrefix(String prefix) {
        return memberRepository.findByNameStartingWith(prefix);
    }

    public void join(JoinRequestDto joinRequestDto) {
        // 해당 name이 존재하는 경우
        if (memberRepository.existsByName(joinRequestDto.getName())) {
            return; // 나중에 예외 처리
        }

        // 유저 객체 생성
        Member member = joinRequestDto.toEntity(bCryptPasswordEncoder);

        memberRepository.save(member);
    }
}