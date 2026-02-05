package com.pointroulette.presentation.admin.roulette

import com.pointroulette.domain.budget.DailyBudget
import com.pointroulette.domain.budget.DailyBudgetRepository
import com.pointroulette.domain.point.Point
import com.pointroulette.domain.point.PointRepository
import com.pointroulette.domain.point.PointSourceType
import com.pointroulette.domain.roulette.RouletteHistory
import com.pointroulette.domain.roulette.RouletteHistoryRepository
import com.pointroulette.domain.roulette.RouletteStatus
import com.pointroulette.domain.user.User
import com.pointroulette.domain.user.UserRepository
import com.pointroulette.helper.DatabaseCleaner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * AdminRouletteController 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DisplayName("AdminRouletteController 통합 테스트")
class AdminRouletteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var rouletteHistoryRepository: RouletteHistoryRepository

    @Autowired
    private lateinit var dailyBudgetRepository: DailyBudgetRepository

    @Autowired
    private lateinit var pointRepository: PointRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var databaseCleaner: DatabaseCleaner

    @BeforeEach
    fun setUp() {
        databaseCleaner.clear()
    }

    @Nested
    @DisplayName("POST /api/v1/admin/roulette/participations/{historyId}/cancel")
    inner class CancelParticipationApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("룰렛 참여를 취소하고 200 응답과 취소된 이력 정보를 반환한다")
            fun `should return 200 with cancelled history when request is valid`() {
                // Given
                val today = LocalDate.now()
                val user = userRepository.save(User("테스트유저"))
                val budget = dailyBudgetRepository.save(DailyBudget(today, 100_000, 99_500))
                val history = rouletteHistoryRepository.save(
                    RouletteHistory(user, today, 500, budget, RouletteStatus.SUCCESS)
                )

                // 포인트 적립 후 저장
                user.updateCurrentPoint(500)
                userRepository.save(user)

                val point = pointRepository.save(
                    Point(
                        user = user,
                        initialAmount = 500,
                        expiresAt = LocalDateTime.now().plusDays(30),
                        sourceType = PointSourceType.ROULETTE,
                        sourceId = history.id
                    )
                )

                // When & Then
                mockMvc.post("/api/v1/admin/roulette/participations/${history.id}/cancel") {
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.id") { value(history.id) }
                    jsonPath("$.data.status") { value("CANCELLED") }
                    jsonPath("$.data.wonAmount") { value(500) }
                    jsonPath("$.data.participatedDate") { value(today.toString()) }
                }

                // 데이터베이스 검증
                val cancelledHistory = rouletteHistoryRepository.findById(history.id).get()
                assertThat(cancelledHistory.status).isEqualTo(RouletteStatus.CANCELLED)

                val updatedBudget = dailyBudgetRepository.findById(budget.id).get()
                assertThat(updatedBudget.remainingAmount).isEqualTo(100_000) // 예산 복구됨

                val updatedUser = userRepository.findById(user.id).get()
                assertThat(updatedUser.currentPoint).isEqualTo(0) // 포인트 회수됨
            }

            @Test
            @DisplayName("예산이 복구된다")
            fun `should restore budget when cancelling participation`() {
                // Given
                val today = LocalDate.now()
                val user = userRepository.save(User("테스트유저"))
                val budget = dailyBudgetRepository.save(DailyBudget(today, 100_000, 50_000))
                val history = rouletteHistoryRepository.save(
                    RouletteHistory(user, today, 750, budget, RouletteStatus.SUCCESS)
                )

                // 포인트 적립 후 저장
                user.updateCurrentPoint(750)
                userRepository.save(user)

                pointRepository.save(
                    Point(
                        user = user,
                        initialAmount = 750,
                        expiresAt = LocalDateTime.now().plusDays(30),
                        sourceType = PointSourceType.ROULETTE,
                        sourceId = history.id
                    )
                )

                // When
                mockMvc.post("/api/v1/admin/roulette/participations/${history.id}/cancel") {
                }.andExpect {
                    status { isOk() }
                }

                // Then
                val updatedBudget = dailyBudgetRepository.findById(budget.id).get()
                assertThat(updatedBudget.remainingAmount).isEqualTo(50_750) // 50,000 + 750
            }

            @Test
            @DisplayName("포인트가 회수된다")
            fun `should reclaim points when cancelling participation`() {
                // Given
                val today = LocalDate.now()
                val user = userRepository.save(User("테스트유저"))
                val budget = dailyBudgetRepository.save(DailyBudget(today, 100_000, 80_000))
                val history = rouletteHistoryRepository.save(
                    RouletteHistory(user, today, 300, budget, RouletteStatus.SUCCESS)
                )

                // 포인트 적립 후 저장
                user.updateCurrentPoint(300)
                userRepository.save(user)

                pointRepository.save(
                    Point(
                        user = user,
                        initialAmount = 300,
                        expiresAt = LocalDateTime.now().plusDays(30),
                        sourceType = PointSourceType.ROULETTE,
                        sourceId = history.id
                    )
                )

                // When
                mockMvc.post("/api/v1/admin/roulette/participations/${history.id}/cancel") {
                }.andExpect {
                    status { isOk() }
                }

                // Then
                val updatedUser = userRepository.findById(user.id).get()
                assertThat(updatedUser.currentPoint).isEqualTo(0) // 300p 회수됨
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        inner class FailureTest {

            @Test
            @DisplayName("존재하지 않는 참여 이력이면 404 에러를 반환한다")
            fun `should return 404 when history not found`() {
                // Given
                val nonExistentHistoryId = 999L

                // When & Then
                mockMvc.post("/api/v1/admin/roulette/participations/$nonExistentHistoryId/cancel") {
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.httpStatus") { value(404) }
                }
            }

            @Test
            @DisplayName("이미 취소된 참여면 400 에러를 반환한다")
            fun `should return 400 when already cancelled`() {
                // Given
                val today = LocalDate.now()
                val user = userRepository.save(User("테스트유저"))
                val budget = dailyBudgetRepository.save(DailyBudget(today, 100_000, 99_500))
                val history = rouletteHistoryRepository.save(
                    RouletteHistory(user, today, 500, budget, RouletteStatus.CANCELLED)
                )

                // When & Then
                mockMvc.post("/api/v1/admin/roulette/participations/${history.id}/cancel") {
                }.andExpect {
                    status { isBadRequest() }
                    jsonPath("$.httpStatus") { value(400) }
                }
            }
        }
    }
}
