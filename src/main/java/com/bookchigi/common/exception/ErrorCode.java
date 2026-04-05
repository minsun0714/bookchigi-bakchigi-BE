package com.bookchigi.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),

    // Book
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "책을 찾을 수 없습니다."),
    NAVER_API_ERROR(HttpStatus.BAD_GATEWAY, "네이버 API 호출에 실패했습니다."),

    // Study
    STUDY_NOT_FOUND(HttpStatus.NOT_FOUND, "스터디를 찾을 수 없습니다."),
    STUDY_FULL(HttpStatus.CONFLICT, "스터디 정원이 가득 찼습니다."),
    STUDY_ALREADY_JOINED(HttpStatus.CONFLICT, "이미 가입한 스터디입니다."),
    STUDY_ENROLLMENT_CLOSED(HttpStatus.BAD_REQUEST, "모집 기간이 아닙니다."),
    NEXT_LEADER_REQUIRED(HttpStatus.BAD_REQUEST, "스터디장 탈퇴 시 다음 리더를 지정해야 합니다.");

    private final HttpStatus status;
    private final String message;
}
