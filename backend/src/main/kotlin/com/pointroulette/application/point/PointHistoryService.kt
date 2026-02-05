package com.pointroulette.application.point

import com.pointroulette.domain.point.*
import com.pointroulette.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 포인트 이력 서비스
 */
@Service
class PointHistoryService(
    private val pointHistoryRepository: PointHistoryRepository
) {

    /**
     * 포인트 적립 이력을 기록합니다.
     *
     * @param user 사용자 엔티티
     * @param point 생성된 Point 엔티티
     * @param amount 적립 금액
     * @param balanceAfter 적립 후 잔액
     * @param sourceType 포인트 획득 경로
     * @param sourceId 포인트 획득 참조 ID
     * @return 생성된 PointHistory 엔티티
     */
    @Transactional
    fun recordEarnHistory(
        user: User,
        point: Point,
        amount: Int,
        balanceAfter: Int,
        sourceType: PointSourceType,
        sourceId: Long
    ): PointHistory {
        val referenceType = when (sourceType) {
            PointSourceType.ROULETTE -> ReferenceType.ROULETTE
            PointSourceType.REFUND -> ReferenceType.ORDER
        }

        val pointHistory = PointHistory(
            user = user,
            point = point,
            amount = amount,
            transactionType = TransactionType.EARN,
            referenceType = referenceType,
            referenceId = sourceId,
            balanceAfter = balanceAfter
        )

        return pointHistoryRepository.save(pointHistory)
    }

    /**
     * 포인트 사용 이력을 기록합니다.
     *
     * @param user 사용자 엔티티
     * @param point 사용된 Point 엔티티
     * @param amount 사용 금액 (음수로 기록)
     * @param balanceAfter 사용 후 잔액
     * @param orderId 주문 ID
     * @return 생성된 PointHistory 엔티티
     */
    @Transactional
    fun recordUseHistory(
        user: User,
        point: Point,
        amount: Int,
        balanceAfter: Int,
        orderId: Long
    ): PointHistory {
        val pointHistory = PointHistory(
            user = user,
            point = point,
            amount = amount,
            transactionType = TransactionType.USE,
            referenceType = ReferenceType.ORDER,
            referenceId = orderId,
            balanceAfter = balanceAfter
        )

        return pointHistoryRepository.save(pointHistory)
    }

    /**
     * 포인트 환불 이력을 기록합니다.
     *
     * @param user 사용자 엔티티
     * @param point 환불된 Point 엔티티
     * @param amount 환불 금액 (양수로 기록)
     * @param balanceAfter 환불 후 잔액
     * @param orderId 주문 ID
     * @return 생성된 PointHistory 엔티티
     */
    @Transactional
    fun recordRefundHistory(
        user: User,
        point: Point,
        amount: Int,
        balanceAfter: Int,
        orderId: Long
    ): PointHistory {
        val pointHistory = PointHistory(
            user = user,
            point = point,
            amount = amount,
            transactionType = TransactionType.REFUND,
            referenceType = ReferenceType.ORDER,
            referenceId = orderId,
            balanceAfter = balanceAfter
        )

        return pointHistoryRepository.save(pointHistory)
    }
}
