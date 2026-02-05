package com.pointroulette.domain.point

/**
 * 포인트 부족 예외
 * - 포인트 사용 시 잔액이 부족한 경우 발생
 *
 * @param currentPoint 현재 보유 포인트
 * @param requestedAmount 요청한 포인트 (음수)
 */
class InsufficientPointException(
    val currentPoint: Int,
    val requestedAmount: Int
) : RuntimeException(
    "포인트가 부족합니다. 현재 포인트: $currentPoint, 필요 포인트: ${-requestedAmount}"
)
