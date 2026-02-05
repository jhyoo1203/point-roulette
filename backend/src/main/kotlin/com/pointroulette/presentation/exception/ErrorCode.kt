package com.pointroulette.presentation.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val httpStatus: HttpStatus, val message: String) {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다."),
    BUSINESS_ERROR(HttpStatus.BAD_REQUEST, "비즈니스 로직 처리 중 오류가 발생했습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스가 DB에 존재하지 않습니다."),

    // 룰렛
    ALREADY_ROULETTE_PARTICIPATED(HttpStatus.BAD_REQUEST, "오늘 이미 룰렛에 참여했습니다."),
    ROULETTE_PARTICIPATION_FAILED(HttpStatus.BAD_REQUEST, "룰렛 참여에 실패했습니다."),
}
