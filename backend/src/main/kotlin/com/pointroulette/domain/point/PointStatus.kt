package com.pointroulette.domain.point

/**
 * 포인트 상태
 */
enum class PointStatus {
    ACTIVE,     // 활성 (사용 가능)
    USED,       // 사용 완료
    EXPIRED,    // 만료됨
    CANCELLED   // 취소됨
}
