package com.pointroulette.domain.point

/**
 * 포인트 거래 유형
 */
enum class TransactionType {
    EARN,       // 획득
    USE,        // 사용
    REFUND,     // 환불
    EXPIRE,     // 만료
    CANCEL      // 취소
}
