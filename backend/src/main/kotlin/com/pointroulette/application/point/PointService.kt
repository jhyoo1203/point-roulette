package com.pointroulette.application.point

import com.pointroulette.application.user.UserService
import com.pointroulette.domain.point.InsufficientPointException
import com.pointroulette.domain.point.Point
import com.pointroulette.domain.point.PointRepository
import com.pointroulette.domain.point.PointSourceType
import com.pointroulette.domain.point.PointStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 포인트 서비스
 */
@Service
class PointService(
    private val pointRepository: PointRepository,
    private val userService: UserService,
    private val pointHistoryService: PointHistoryService
) {

    /**
     * 포인트를 적립합니다.
     * - User의 currentPoint가 증가합니다.
     * - 적립된 포인트는 30일 후 만료됩니다.
     * - 적립 시각은 BaseEntity의 createdAt에 자동 기록됩니다.
     * - 포인트 적립 이력(PointHistory)이 기록됩니다.
     *
     * @param userId 사용자 ID
     * @param amount 적립할 포인트 금액 (0보다 커야 함)
     * @param sourceType 포인트 획득 경로
     * @param sourceId 포인트 획득 참조 ID
     * @return 생성된 Point 엔티티
     * @throws IllegalArgumentException amount가 0 이하인 경우
     */
    @Transactional
    fun earnPoint(
        userId: Long,
        amount: Int,
        sourceType: PointSourceType,
        sourceId: Long
    ): Point {
        require(amount > 0) { "적립 포인트는 0보다 커야 합니다" }

        val user = userService.getUser(userId)
        user.updateCurrentPoint(amount)

        // 30일 후 만료되는 Point 생성 및 저장
        val now = LocalDateTime.now()
        val point = Point(
            user = user,
            initialAmount = amount,
            expiresAt = now.plusDays(30),
            sourceType = sourceType,
            sourceId = sourceId
        )

        val savedPoint = pointRepository.save(point)

        // 포인트 적립 이력 기록
        pointHistoryService.recordEarnHistory(
            user = user,
            point = savedPoint,
            amount = amount,
            balanceAfter = user.currentPoint,
            sourceType = sourceType,
            sourceId = sourceId
        )

        return savedPoint
    }

    /**
     * 포인트를 사용합니다 (FIFO 방식).
     * - 오래된 포인트부터 차감합니다.
     * - User의 currentPoint가 감소합니다.
     * - 사용된 포인트는 remainingAmount가 감소하고, 0이 되면 USED 상태로 변경됩니다.
     * - 포인트 사용 이력(PointHistory)이 기록됩니다.
     *
     * @param userId 사용자 ID
     * @param amount 사용할 포인트 금액 (0보다 커야 함)
     * @param orderId 주문 ID
     * @throws IllegalArgumentException amount가 0 이하인 경우
     * @throws InsufficientPointException 포인트 부족으로 사용할 수 없는 경우
     */
    @Transactional
    fun usePoints(
        userId: Long,
        amount: Int,
        orderId: Long
    ) {
        require(amount > 0) { "사용 포인트는 0보다 커야 합니다" }

        val user = userService.getUser(userId)
        val availablePoints = pointRepository.findAvailablePointsByUserId(userId)
        var remainingAmount = amount

        // 오래된 포인트부터 차감
        for (point in availablePoints) {
            if (remainingAmount <= 0) break

            val deductAmount = minOf(remainingAmount, point.remainingAmount)
            point.remainingAmount -= deductAmount
            remainingAmount -= deductAmount

            // 포인트를 모두 사용한 경우 상태 변경
            if (point.remainingAmount == 0) {
                point.status = PointStatus.USED
            }

            // 사용 이력 기록 (음수로 기록)
            pointHistoryService.recordUseHistory(
                user = user,
                point = point,
                amount = -deductAmount,
                balanceAfter = user.currentPoint - deductAmount,
                orderId = orderId
            )

            // User의 현재 포인트 감소
            user.updateCurrentPoint(-deductAmount)
        }

        if (remainingAmount > 0) {
            throw InsufficientPointException(user.currentPoint, amount)
        }
    }

    /**
     * 포인트를 환불합니다.
     * - User의 currentPoint가 증가합니다.
     * - 환불된 포인트는 30일 후 만료됩니다.
     * - 포인트 환불 이력(PointHistory)이 기록됩니다.
     *
     * @param userId 사용자 ID
     * @param amount 환불할 포인트 금액 (0보다 커야 함)
     * @param orderId 주문 ID
     * @return 생성된 Point 엔티티
     * @throws IllegalArgumentException amount가 0 이하인 경우
     */
    @Transactional
    fun refundPoints(
        userId: Long,
        amount: Int,
        orderId: Long
    ): Point {
        require(amount > 0) { "환불 포인트는 0보다 커야 합니다" }

        val user = userService.getUser(userId)
        user.updateCurrentPoint(amount)

        // 30일 후 만료되는 환불 포인트 생성
        val now = LocalDateTime.now()
        val point = Point(
            user = user,
            initialAmount = amount,
            expiresAt = now.plusDays(30),
            sourceType = PointSourceType.REFUND,
            sourceId = orderId
        )

        val savedPoint = pointRepository.save(point)

        // 환불 이력 기록 (양수로 기록)
        pointHistoryService.recordRefundHistory(
            user = user,
            point = savedPoint,
            amount = amount,
            balanceAfter = user.currentPoint,
            orderId = orderId
        )

        return savedPoint
    }

    /**
     * 포인트를 회수합니다 (룰렛 참여 취소 시).
     * - User의 currentPoint가 감소합니다.
     * - 특정 sourceId로 획득한 포인트를 찾아서 삭제합니다.
     * - 포인트 회수 이력(PointHistory)이 기록됩니다.
     *
     * @param userId 사용자 ID
     * @param amount 회수할 포인트 금액 (0보다 커야 함)
     * @param sourceType 포인트 획득 경로
     * @param sourceId 포인트 획득 참조 ID
     * @throws IllegalArgumentException amount가 0 이하인 경우
     * @throws InsufficientPointException 포인트 부족으로 회수할 수 없는 경우
     */
    @Transactional
    fun reclaimPoints(
        userId: Long,
        amount: Int,
        sourceType: PointSourceType,
        sourceId: Long
    ) {
        require(amount > 0) { "회수 포인트는 0보다 커야 합니다" }

        val user = userService.getUser(userId)

        // 해당 sourceId로 획득한 포인트 찾기
        val pointToReclaim = pointRepository.findByUserIdAndSourceTypeAndSourceId(userId, sourceType, sourceId)
            ?: throw IllegalStateException("회수할 포인트를 찾을 수 없습니다. (sourceId: $sourceId)")

        // 포인트가 일부 또는 전체 사용된 경우 고려
        val reclaimAmount = minOf(amount, user.currentPoint)

        if (reclaimAmount < amount) {
            throw InsufficientPointException(user.currentPoint, amount)
        }

        // User의 현재 포인트 감소
        user.updateCurrentPoint(-reclaimAmount)

        // 포인트 엔티티 상태 변경 (취소됨)
        pointToReclaim.status = PointStatus.CANCELLED
        pointToReclaim.remainingAmount = 0

        // 회수 이력 기록 (음수로 기록)
        pointHistoryService.recordReclaimHistory(
            user = user,
            point = pointToReclaim,
            amount = -reclaimAmount,
            balanceAfter = user.currentPoint,
            sourceType = sourceType,
            sourceId = sourceId
        )
    }
}
