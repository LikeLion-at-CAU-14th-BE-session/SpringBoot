package com.example.likelion14th_springboot.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User, Member 관련 예외 - M
    DUPLICATE_NAME(HttpStatus.CONFLICT, "M-001", "이미 사용 중인 이름입니다."),

    // Server 예외 - S
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-001", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}