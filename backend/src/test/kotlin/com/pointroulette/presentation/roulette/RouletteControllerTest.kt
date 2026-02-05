package com.pointroulette.presentation.roulette

import com.fasterxml.jackson.databind.ObjectMapper
import com.pointroulette.domain.budget.DailyBudget
import com.pointroulette.domain.budget.DailyBudgetRepository
import com.pointroulette.domain.point.PointHistoryRepository
import com.pointroulette.domain.point.PointRepository
import com.pointroulette.domain.roulette.RouletteHistoryRepository
import com.pointroulette.domain.user.User
import com.pointroulette.domain.user.UserRepository
import com.pointroulette.helper.DatabaseCleaner
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

/**
 * RouletteController 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DisplayName("RouletteController 통합 테스트")
class RouletteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var dailyBudgetRepository: DailyBudgetRepository

    @Autowired
    private lateinit var rouletteHistoryRepository: RouletteHistoryRepository

    @Autowired
    private lateinit var pointRepository: PointRepository

    @Autowired
    private lateinit var pointHistoryRepository: PointHistoryRepository

    @Autowired
    private lateinit var databaseCleaner: DatabaseCleaner

    @BeforeEach
    fun setUp() {
        databaseCleaner.clear()
    }

    @Nested
    @DisplayName("POST /api/v1/roulette/participate/{userId}")
    inner class ParticipateApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("정상적으로 룰렛에 참여하고 포인트를 획득한다")
            fun `should participate in roulette and return won amount`() {
                // tiven
                val user = userRepository.save(User("테스트유저"))
                val today = LocalDate.now()
                dailyBudgetRepository.save(DailyBudget(today, 100_000, 100_000))

                // when & then
                mockMvc.post("/api/v1/roulette/participate/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.success") { value(true) }
                    jsonPath("$.data.wonAmount") { exists() }
                    jsonPath("$.data.remainingBudget") { exists() }
                }

                // 룰렛 참여 이력이 생성되었는지 확인
                val hasParticipated = rouletteHistoryRepository
                    .existsByUserIdAndParticipatedDate(user.id, today)
                Assertions.assertThat(hasParticipated).isTrue()
            }

            @Test
            @DisplayName("참여 후 사용자 포인트가 증가한다")
            fun `should increase user point after participation`() {
                // given
                val user = userRepository.save(User("테스트유저"))
                val today = LocalDate.now()
                dailyBudgetRepository.save(DailyBudget(today, 100_000, 100_000))

                val initialPoint = user.currentPoint

                // when
                mockMvc.post("/api/v1/roulette/participate/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                }

                // then
                val updatedUser = userRepository.findById(user.id).get()
                Assertions.assertThat(updatedUser.currentPoint).isGreaterThan(initialPoint)
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        inner class FailureTest {

            @Test
            @DisplayName("오늘 이미 참여한 경우 409 에러를 반환한다")
            fun `should return 409 when already participated today`() {
                // given
                val user = userRepository.save(User("테스트유저"))
                val today = LocalDate.now()
                // 예산을 충분히 크게 설정 (두 번 참여 시도 시 예산 부족이 아닌 중복 참여 에러가 나도록)
                dailyBudgetRepository.save(DailyBudget(today, 1_000_000, 1_000_000))

                // 첫 번째 참여
                mockMvc.post("/api/v1/roulette/participate/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                }

                // when & then: 두 번째 참여 시도
                mockMvc.post("/api/v1/roulette/participate/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isConflict() }
                    jsonPath("$.httpStatus") { value(409) }
                    jsonPath("$.errorCode") { value("ALREADY_ROULETTE_PARTICIPATED") }
                }
            }

            @Test
            @DisplayName("존재하지 않는 사용자는 404 에러를 반환한다")
            fun `should return 404 when user not found`() {
                // given
                val nonExistentUserId = 99999L
                val today = LocalDate.now()
                dailyBudgetRepository.save(DailyBudget(today, 100_000, 100_000))

                // when & then
                mockMvc.post("/api/v1/roulette/participate/$nonExistentUserId") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.httpStatus") { value(404) }
                }
            }

            @Test
            @DisplayName("오늘의 예산이 없으면 404 에러를 반환한다")
            fun `should return 404 when today budget not found`() {
                // given
                val user = userRepository.save(User("테스트유저"))
                // 예산을 생성하지 않음

                // when & then
                mockMvc.post("/api/v1/roulette/participate/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.httpStatus") { value(404) }
                }
            }

            @Test
            @DisplayName("예산이 부족하면 400 에러를 반환한다")
            fun `should return 400 when budget is insufficient`() {
                // given
                val user = userRepository.save(User("테스트유저"))
                val today = LocalDate.now()
                // 예산을 0으로 설정
                dailyBudgetRepository.save(DailyBudget(today, 100_000, 0))

                // when & then
                mockMvc.post("/api/v1/roulette/participate/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isBadRequest() }
                    jsonPath("$.httpStatus") { value(400) }
                }
            }
        }
    }

    @Nested
    @DisplayName("GET /api/v1/roulette/status/{userId}")
    inner class GetStatusApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("오늘 참여하지 않은 경우 hasParticipatedToday가 false이다")
            fun `should return hasParticipatedToday false when not participated`() {
                // Given
                val user = userRepository.save(User("테스트유저"))
                val today = LocalDate.now()
                dailyBudgetRepository.save(DailyBudget(today, 100_000, 100_000))

                // When & Then
                mockMvc.get("/api/v1/roulette/status/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.hasParticipatedToday") { value(false) }
                    jsonPath("$.data.todayRemainingBudget") { value(100_000) }
                    jsonPath("$.data.lastParticipation") { doesNotExist() }
                }
            }

            @Test
            @DisplayName("오늘 참여한 경우 hasParticipatedToday가 true이고 참여 정보를 반환한다")
            fun `should return hasParticipatedToday true with participation info`() {
                // Given
                val user = userRepository.save(User("테스트유저"))
                val today = LocalDate.now()
                dailyBudgetRepository.save(DailyBudget(today, 100_000, 100_000))

                // 룰렛 참여
                mockMvc.post("/api/v1/roulette/participate/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                }

                // When & Then
                mockMvc.get("/api/v1/roulette/status/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.hasParticipatedToday") { value(true) }
                    jsonPath("$.data.todayRemainingBudget") { exists() }
                    jsonPath("$.data.lastParticipation") { exists() }
                    jsonPath("$.data.lastParticipation.wonAmount") { exists() }
                    jsonPath("$.data.lastParticipation.participatedDate") { exists() }
                }
            }

            @Test
            @DisplayName("예산이 모두 소진된 경우에도 상태를 조회할 수 있다")
            fun `should return status even when budget is exhausted`() {
                // Given
                val user = userRepository.save(User("테스트유저"))
                val today = LocalDate.now()
                dailyBudgetRepository.save(DailyBudget(today, 100_000, 0))

                // When & Then
                mockMvc.get("/api/v1/roulette/status/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.hasParticipatedToday") { value(false) }
                    jsonPath("$.data.todayRemainingBudget") { value(0) }
                }
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        inner class FailureTest {

            @Test
            @DisplayName("존재하지 않는 사용자는 404 에러를 반환한다")
            fun `should return 404 when user not found`() {
                // Given
                val nonExistentUserId = 99999L
                val today = LocalDate.now()
                dailyBudgetRepository.save(DailyBudget(today, 100_000, 100_000))

                // When & Then
                mockMvc.get("/api/v1/roulette/status/$nonExistentUserId") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.httpStatus") { value(404) }
                }
            }

            @Test
            @DisplayName("오늘의 예산이 없으면 404 에러를 반환한다")
            fun `should return 404 when today budget not found`() {
                // Given
                val user = userRepository.save(User("테스트유저"))
                // 예산을 생성하지 않음

                // When & Then
                mockMvc.get("/api/v1/roulette/status/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.httpStatus") { value(404) }
                }
            }
        }
    }
}