package com.pointroulette.presentation.point

import com.pointroulette.domain.point.Point
import com.pointroulette.domain.point.PointRepository
import com.pointroulette.domain.point.PointSourceType
import com.pointroulette.domain.point.PointStatus
import com.pointroulette.domain.user.User
import com.pointroulette.domain.user.UserRepository
import com.pointroulette.helper.DatabaseCleaner
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
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

/**
 * PointController 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DisplayName("PointController 통합 테스트")
class PointControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

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
    @DisplayName("GET /api/v1/points/{userId}")
    inner class GetPointBalanceApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("사용자의 포인트 잔액과 포인트 목록을 조회한다")
            fun `should return point balance and point list`() {
                // Given
                val user = userRepository.save(
                    User(nickname = "testuser", currentPoint = 5000)
                )

                val now = LocalDateTime.now()
                pointRepository.save(
                    Point(
                        user = user,
                        initialAmount = 2000,
                        expiresAt = now.plusDays(10),
                        sourceType = PointSourceType.ROULETTE,
                        sourceId = 1L
                    )
                )
                pointRepository.save(
                    Point(
                        user = user,
                        initialAmount = 3000,
                        expiresAt = now.plusDays(20),
                        sourceType = PointSourceType.ROULETTE,
                        sourceId = 2L
                    )
                )

                // When & Then
                mockMvc.get("/api/v1/points/${user.id}") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.userId") { value(user.id) }
                    jsonPath("$.data.currentPoint") { value(5000) }
                    jsonPath("$.data.points.length()") { value(2) }
                    jsonPath("$.data.points[0].initialAmount") { value(2000) }
                    jsonPath("$.data.points[1].initialAmount") { value(3000) }
                }
            }

            @Test
            @DisplayName("7일 이내 만료 예정 포인트를 정확하게 계산한다")
            fun `should calculate expiring points within 7 days correctly`() {
                // Given
                val user = userRepository.save(
                    User(nickname = "testuser", currentPoint = 3000)
                )

                val now = LocalDateTime.now()
                // 3일 후 만료
                pointRepository.save(
                    Point(
                        user = user,
                        initialAmount = 500,
                        expiresAt = now.plusDays(3),
                        sourceType = PointSourceType.ROULETTE,
                        sourceId = 1L
                    )
                )
                // 5일 후 만료
                pointRepository.save(
                    Point(
                        user = user,
                        initialAmount = 500,
                        expiresAt = now.plusDays(5),
                        sourceType = PointSourceType.ROULETTE,
                        sourceId = 2L
                    )
                )
                // 20일 후 만료
                pointRepository.save(
                    Point(
                        user = user,
                        initialAmount = 2000,
                        expiresAt = now.plusDays(20),
                        sourceType = PointSourceType.ROULETTE,
                        sourceId = 3L
                    )
                )

                // When & Then
                mockMvc.get("/api/v1/points/${user.id}") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.userId") { value(user.id) }
                    jsonPath("$.data.currentPoint") { value(3000) }
                    jsonPath("$.data.expiringPointIn7Days") { value(1000) } // 500 + 500
                    jsonPath("$.data.points.length()") { value(3) }
                }
            }

            @Test
            @DisplayName("포인트가 없는 사용자는 빈 목록을 반환한다")
            fun `should return empty point list when user has no points`() {
                // Given
                val user = userRepository.save(
                    User(nickname = "newuser", currentPoint = 0)
                )

                // When & Then
                mockMvc.get("/api/v1/points/${user.id}") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.userId") { value(user.id) }
                    jsonPath("$.data.currentPoint") { value(0) }
                    jsonPath("$.data.expiringPointIn7Days") { value(0) }
                    jsonPath("$.data.points.length()") { value(0) }
                }
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        inner class FailureTest {

            @Test
            @DisplayName("존재하지 않는 사용자는 404 오류를 반환한다")
            fun `should return 404 when user not found`() {
                // Given
                val nonExistentUserId = 999L

                // When & Then
                mockMvc.get("/api/v1/points/$nonExistentUserId") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.httpStatus") { value(404) }
                }
            }
        }
    }
}
