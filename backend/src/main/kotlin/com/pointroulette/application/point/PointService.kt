package com.pointroulette.application.point

import com.pointroulette.application.user.UserService
import com.pointroulette.domain.point.Point
import com.pointroulette.domain.point.PointRepository
import com.pointroulette.domain.point.PointSourceType
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
}
