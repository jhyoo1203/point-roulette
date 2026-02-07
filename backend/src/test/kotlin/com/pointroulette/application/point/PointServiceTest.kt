package com.pointroulette.application.point

import com.pointroulette.application.point.dto.PointBalanceResponse
import com.pointroulette.application.user.UserService
import com.pointroulette.domain.point.Point
import com.pointroulette.domain.point.PointAlreadyUsedException
import com.pointroulette.domain.point.PointRepository
import com.pointroulette.domain.point.PointSourceType
import com.pointroulette.domain.point.PointStatus
import com.pointroulette.domain.user.User
import com.pointroulette.helper.TestEntityFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
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

    @Nested
    @DisplayName("reclaimPoints 메서드")
    inner class ReclaimPointsTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("사용되지 않은 포인트를 정상적으로 회수한다")
            fun `should reclaim unused points successfully`() {
                // given
                val userId = 1L
                val amount = 500
                val sourceType = PointSourceType.ROULETTE
                val sourceId = 1L

                val user = TestEntityFactory.createUser(
                    id = userId,
                    nickname = "testuser",
                    currentPoint = 1000
                )

                val now = LocalDateTime.now()
                val pointToReclaim = TestEntityFactory.createPoint(
                    id = 1L,
                    user = user,
                    initialAmount = 500,
                    remainingAmount = 500, // 사용되지 않음
                    expiresAt = now.plusDays(30),
                    sourceType = sourceType,
                    sourceId = sourceId,
                    status = PointStatus.ACTIVE
                )

                given(userService.getUser(userId)).willReturn(user)
                given(pointRepository.findByUserIdAndSourceTypeAndSourceId(userId, sourceType, sourceId))
                    .willReturn(pointToReclaim)

                // when
                pointService.reclaimPoints(userId, amount, sourceType, sourceId)

                // then
                assertThat(user.currentPoint).isEqualTo(500) // 1000 - 500
                assertThat(pointToReclaim.status).isEqualTo(PointStatus.CANCELLED)
                assertThat(pointToReclaim.remainingAmount).isEqualTo(0)

                verify(userService).getUser(userId)
                verify(pointRepository).findByUserIdAndSourceTypeAndSourceId(userId, sourceType, sourceId)
                verify(pointHistoryService).recordReclaimHistory(
                    user = eq(user),
                    point = eq(pointToReclaim),
                    amount = eq(-amount),
                    balanceAfter = eq(500),
                    sourceType = eq(sourceType),
                    sourceId = eq(sourceId)
                )
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        inner class FailureTest {

            @Test
            @DisplayName("포인트가 일부 사용된 경우 PointAlreadyUsedException 예외를 발생시킨다")
            fun `should throw PointAlreadyUsedException when points are partially used`() {
                // given
                val userId = 1L
                val amount = 500
                val sourceType = PointSourceType.ROULETTE
                val sourceId = 1L

                val user = TestEntityFactory.createUser(
                    id = userId,
                    nickname = "testuser",
                    currentPoint = 800
                )

                val now = LocalDateTime.now()
                val pointToReclaim = TestEntityFactory.createPoint(
                    id = 1L,
                    user = user,
                    initialAmount = 500,
                    remainingAmount = 300, // 일부 사용됨
                    expiresAt = now.plusDays(30),
                    sourceType = sourceType,
                    sourceId = sourceId,
                    status = PointStatus.ACTIVE
                )

                given(userService.getUser(userId)).willReturn(user)
                given(pointRepository.findByUserIdAndSourceTypeAndSourceId(userId, sourceType, sourceId))
                    .willReturn(pointToReclaim)

                // when & then
                assertThatThrownBy { pointService.reclaimPoints(userId, amount, sourceType, sourceId) }
                    .isInstanceOf(PointAlreadyUsedException::class.java)
                    .hasMessageContaining("이미 사용된 포인트는 회수할 수 없습니다")

                verify(userService).getUser(userId)
                verify(pointRepository).findByUserIdAndSourceTypeAndSourceId(userId, sourceType, sourceId)
            }

            @Test
            @DisplayName("포인트가 전부 사용된 경우 PointAlreadyUsedException 예외를 발생시킨다")
            fun `should throw PointAlreadyUsedException when points are fully used`() {
                // given
                val userId = 1L
                val amount = 500
                val sourceType = PointSourceType.ROULETTE
                val sourceId = 1L

                val user = TestEntityFactory.createUser(
                    id = userId,
                    nickname = "testuser",
                    currentPoint = 500
                )

                val now = LocalDateTime.now()
                val pointToReclaim = TestEntityFactory.createPoint(
                    id = 1L,
                    user = user,
                    initialAmount = 500,
                    remainingAmount = 0, // 전부 사용됨
                    expiresAt = now.plusDays(30),
                    sourceType = sourceType,
                    sourceId = sourceId,
                    status = PointStatus.USED
                )

                given(userService.getUser(userId)).willReturn(user)
                given(pointRepository.findByUserIdAndSourceTypeAndSourceId(userId, sourceType, sourceId))
                    .willReturn(pointToReclaim)

                // when & then
                assertThatThrownBy { pointService.reclaimPoints(userId, amount, sourceType, sourceId) }
                    .isInstanceOf(PointAlreadyUsedException::class.java)
                    .hasMessageContaining("이미 사용된 포인트는 회수할 수 없습니다")

                verify(userService).getUser(userId)
                verify(pointRepository).findByUserIdAndSourceTypeAndSourceId(userId, sourceType, sourceId)
            }

            @Test
            @DisplayName("회수할 포인트를 찾을 수 없는 경우 IllegalStateException 예외를 발생시킨다")
            fun `should throw IllegalStateException when point not found`() {
                // given
                val userId = 1L
                val amount = 500
                val sourceType = PointSourceType.ROULETTE
                val sourceId = 999L // 존재하지 않는 sourceId

                val user = TestEntityFactory.createUser(
                    id = userId,
                    nickname = "testuser",
                    currentPoint = 1000
                )

                given(userService.getUser(userId)).willReturn(user)
                given(pointRepository.findByUserIdAndSourceTypeAndSourceId(userId, sourceType, sourceId))
                    .willReturn(null)

                // when & then
                assertThatThrownBy { pointService.reclaimPoints(userId, amount, sourceType, sourceId) }
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("회수할 포인트를 찾을 수 없습니다")

                verify(userService).getUser(userId)
                verify(pointRepository).findByUserIdAndSourceTypeAndSourceId(userId, sourceType, sourceId)
            }
        }
    }
}
