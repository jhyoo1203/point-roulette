package com.pointroulette.domain.point

import com.pointroulette.presentation.exception.BusinessException
import com.pointroulette.presentation.exception.ErrorCode

/**
 * 이미 사용된 포인트 예외
 * - 룰렛 포인트 회수 시 해당 포인트가 이미 일부 또는 전부 사용된 경우 발생
 *
 * @param initialAmount 최초 적립 금액
 * @param remainingAmount 남은 포인트
 */
class PointAlreadyUsedException(
    val initialAmount: Int,
    val remainingAmount: Int
) : BusinessException(
    ErrorCode.POINT_ALREADY_USED,
    "이미 사용된 포인트는 회수할 수 없습니다. 최초 금액: $initialAmount, 남은 금액: $remainingAmount"
)
