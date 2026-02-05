package com.pointroulette.application.point

import com.pointroulette.application.point.dto.PointBalanceResponse
import com.pointroulette.application.user.UserService
import com.pointroulette.domain.point.Point
import com.pointroulette.domain.point.PointRepository
import com.pointroulette.domain.point.PointSourceType
import com.pointroulette.domain.point.PointStatus
import com.pointroulette.domain.user.User
import com.pointroulette.helper.TestEntityFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

/**
 * PointService 단위 테스트
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("PointService 테스트")
class PointServiceTest {

    @Mock
    private lateinit var pointRepository: PointRepository

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var pointHistoryService: PointHistoryService

    @InjectMocks
    private lateinit var pointService: PointService

    @Nested
    @DisplayName("getPointBalance 메서드")
    inner class GetPointBalanceTest {

        @Test
        @DisplayName("사용자의 포인트 잔액과 포인트 목록을 조회한다")
        fun `should return point balance and point list`() {
            // Given
            val userId = 1L
            val user = TestEntityFactory.createUser(
                id = userId,
                nickname = "testuser",
                currentPoint = 5000
            )

            val now = LocalDateTime.now()
            val point1 = TestEntityFactory.createPoint(
                id = 1L,
                user = user,
                initialAmount = 1000,
                remainingAmount = 1000,
                expiresAt = now.plusDays(10),
                sourceType = PointSourceType.ROULETTE,
                sourceId = 1L,
                status = PointStatus.ACTIVE
            )
            val point2 = TestEntityFactory.createPoint(
                id = 2L,
                user = user,
                initialAmount = 2000,
                remainingAmount = 2000,
                expiresAt = now.plusDays(20),
                sourceType = PointSourceType.ROULETTE,
                sourceId = 2L,
                status = PointStatus.ACTIVE
            )
            val point3 = TestEntityFactory.createPoint(
                id = 3L,
                user = user,
                initialAmount = 2000,
                remainingAmount = 2000,
                expiresAt = now.plusDays(25),
                sourceType = PointSourceType.REFUND,
                sourceId = 1L,
                status = PointStatus.ACTIVE
            )

            val allPoints = listOf(point1, point2, point3)
            val expiringPoints = emptyList<Point>()

            given(userService.getUser(userId)).willReturn(user)
            given(pointRepository.findAllByUserId(userId)).willReturn(allPoints)
            given(pointRepository.findExpiringPointsIn7Days(userId)).willReturn(expiringPoints)

            // When
            val response = pointService.getPointBalance(userId)

            // Then
            assertThat(response.userId).isEqualTo(userId)
            assertThat(response.currentPoint).isEqualTo(5000)
            assertThat(response.expiringPointIn7Days).isEqualTo(0)
            assertThat(response.points).hasSize(3)
            assertThat(response.points[0].id).isEqualTo(1L)
            assertThat(response.points[1].id).isEqualTo(2L)
            assertThat(response.points[2].id).isEqualTo(3L)
        }

        @Test
        @DisplayName("7일 이내 만료 예정 포인트를 정확하게 계산한다")
        fun `should calculate expiring points within 7 days correctly`() {
            // Given
            val userId = 1L
            val user = TestEntityFactory.createUser(
                id = userId,
                nickname = "testuser",
                currentPoint = 3000
            )

            val now = LocalDateTime.now()
            val expiringPoint1 = TestEntityFactory.createPoint(
                id = 1L,
                user = user,
                initialAmount = 500,
                remainingAmount = 500,
                expiresAt = now.plusDays(3),
                sourceType = PointSourceType.ROULETTE,
                sourceId = 1L,
                status = PointStatus.ACTIVE
            )
            val expiringPoint2 = TestEntityFactory.createPoint(
                id = 2L,
                user = user,
                initialAmount = 500,
                remainingAmount = 500,
                expiresAt = now.plusDays(5),
                sourceType = PointSourceType.ROULETTE,
                sourceId = 2L,
                status = PointStatus.ACTIVE
            )
            val normalPoint = TestEntityFactory.createPoint(
                id = 3L,
                user = user,
                initialAmount = 2000,
                remainingAmount = 2000,
                expiresAt = now.plusDays(20),
                sourceType = PointSourceType.ROULETTE,
                sourceId = 3L,
                status = PointStatus.ACTIVE
            )

            val allPoints = listOf(expiringPoint1, expiringPoint2, normalPoint)
            val expiringPoints = listOf(expiringPoint1, expiringPoint2)

            given(userService.getUser(userId)).willReturn(user)
            given(pointRepository.findAllByUserId(userId)).willReturn(allPoints)
            given(pointRepository.findExpiringPointsIn7Days(userId)).willReturn(expiringPoints)

            // When
            val response = pointService.getPointBalance(userId)

            // Then
            assertThat(response.userId).isEqualTo(userId)
            assertThat(response.currentPoint).isEqualTo(3000)
            assertThat(response.expiringPointIn7Days).isEqualTo(1000) // 500 + 500
            assertThat(response.points).hasSize(3)
        }

        @Test
        @DisplayName("포인트가 없는 사용자는 빈 목록을 반환한다")
        fun `should return empty point list when user has no points`() {
            // Given
            val userId = 1L
            val user = TestEntityFactory.createUser(
                id = userId,
                nickname = "newuser",
                currentPoint = 0
            )

            given(userService.getUser(userId)).willReturn(user)
            given(pointRepository.findAllByUserId(userId)).willReturn(emptyList())
            given(pointRepository.findExpiringPointsIn7Days(userId)).willReturn(emptyList())

            // When
            val response = pointService.getPointBalance(userId)

            // Then
            assertThat(response.userId).isEqualTo(userId)
            assertThat(response.currentPoint).isEqualTo(0)
            assertThat(response.expiringPointIn7Days).isEqualTo(0)
            assertThat(response.points).isEmpty()
        }
    }
}
