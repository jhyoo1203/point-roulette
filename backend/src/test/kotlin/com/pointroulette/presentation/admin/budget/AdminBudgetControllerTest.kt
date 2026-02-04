package com.pointroulette.presentation.admin.budget

import com.fasterxml.jackson.databind.ObjectMapper
import com.pointroulette.application.budget.dto.DailyBudgetCreateRequest
import com.pointroulette.domain.budget.DailyBudget
import com.pointroulette.domain.budget.DailyBudgetRepository
import org.assertj.core.api.Assertions.assertThat
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
 * AdminBudgetController 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DisplayName("AdminBudgetController 통합 테스트")
class AdminBudgetControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var dailyBudgetRepository: DailyBudgetRepository

    @BeforeEach
    fun setUp() {
        dailyBudgetRepository.deleteAll()
    }

    @Nested
    @DisplayName("POST /api/v1/admin/budgets")
    inner class CreateDailyBudgetsApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("유효한 날짜 범위 요청이면 201 응답과 생성된 예산 목록을 반환한다")
            fun `should return 201 with created budgets when request is valid`() {
                // Given
                val startDate = LocalDate.of(2026, 1, 1)
                val endDate = LocalDate.of(2026, 1, 5)
                val request = DailyBudgetCreateRequest(startDate, endDate)

                // When & Then
                mockMvc.post("/api/v1/admin/budgets") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.httpStatus") { value(201) }
                    jsonPath("$.data.length()") { value(5) }
                    jsonPath("$.data[0].budgetDate") { value("2026-01-01") }
                    jsonPath("$.data[0].totalAmount") { value(100000) }
                    jsonPath("$.data[0].remainingAmount") { value(100000) }
                    jsonPath("$.data[4].budgetDate") { value("2026-01-05") }
                    jsonPath("$.data[0].id") { exists() }
                    jsonPath("$.data[0].createdAt") { exists() }
                    jsonPath("$.data[0].updatedAt") { exists() }
                }

                // 데이터베이스에 예산이 생성되었는지 확인
                assertThat(dailyBudgetRepository.count()).isEqualTo(5)
                val savedBudgets = dailyBudgetRepository.findAll()
                assertThat(savedBudgets).hasSize(5)
                assertThat(savedBudgets.map { it.budgetDate }).containsExactlyInAnyOrder(
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 1, 2),
                    LocalDate.of(2026, 1, 3),
                    LocalDate.of(2026, 1, 4),
                    LocalDate.of(2026, 1, 5)
                )
            }

            @Test
            @DisplayName("단일 날짜 예산을 생성할 수 있다")
            fun `should create single day budget`() {
                // Given
                val targetDate = LocalDate.of(2026, 3, 15)
                val request = DailyBudgetCreateRequest(targetDate, targetDate)

                // When & Then
                mockMvc.post("/api/v1/admin/budgets") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.httpStatus") { value(201) }
                    jsonPath("$.data.length()") { value(1) }
                    jsonPath("$.data[0].budgetDate") { value("2026-03-15") }
                }

                assertThat(dailyBudgetRepository.count()).isEqualTo(1)
            }

            @Test
            @DisplayName("이미 존재하는 날짜는 건너뛰고 새로운 날짜만 생성한다")
            fun `should skip existing dates and create only new dates`() {
                // Given
                // 1월 2일 예산 미리 생성
                dailyBudgetRepository.save(
                    DailyBudget(LocalDate.of(2026, 1, 2), 100_000, 100_000)
                )

                val startDate = LocalDate.of(2026, 1, 1)
                val endDate = LocalDate.of(2026, 1, 3)
                val request = DailyBudgetCreateRequest(startDate, endDate)

                // When & Then
                mockMvc.post("/api/v1/admin/budgets") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.httpStatus") { value(201) }
                    jsonPath("$.data.length()") { value(2) }
                }

                // 전체 3개여야 함 (기존 1개 + 신규 2개)
                assertThat(dailyBudgetRepository.count()).isEqualTo(3)
                val savedBudgets = dailyBudgetRepository.findAll()
                assertThat(savedBudgets.map { it.budgetDate }).containsExactlyInAnyOrder(
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 1, 2),
                    LocalDate.of(2026, 1, 3)
                )
            }

            @Test
            @DisplayName("모든 날짜가 이미 존재하면 빈 목록을 반환한다")
            fun `should return empty list when all dates already exist`() {
                // Given
                dailyBudgetRepository.save(
                    DailyBudget(LocalDate.of(2026, 1, 1), 100_000, 100_000)
                )
                dailyBudgetRepository.save(
                    DailyBudget(LocalDate.of(2026, 1, 2), 100_000, 100_000)
                )

                val request = DailyBudgetCreateRequest(
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 1, 2)
                )

                // When & Then
                mockMvc.post("/api/v1/admin/budgets") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.httpStatus") { value(201) }
                    jsonPath("$.data.length()") { value(0) }
                }

                assertThat(dailyBudgetRepository.count()).isEqualTo(2)
            }
        }

        @Nested
        @DisplayName("실패 케이스 - 유효성 검증")
        inner class ValidationFailTest {

            @Test
            @DisplayName("시작일이 null이면 400 에러를 반환한다")
            fun `should return 400 when startDate is null`() {
                // Given
                val request = mapOf(
                    "endDate" to "2026-01-31"
                )

                // When & Then
                mockMvc.post("/api/v1/admin/budgets") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("종료일이 null이면 400 에러를 반환한다")
            fun `should return 400 when endDate is null`() {
                // Given
                val request = mapOf(
                    "startDate" to "2026-01-01"
                )

                // When & Then
                mockMvc.post("/api/v1/admin/budgets") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("시작일이 종료일보다 이후면 400 에러를 반환한다")
            fun `should return 400 when startDate is after endDate`() {
                // Given
                val request = mapOf(
                    "startDate" to "2026-01-31",
                    "endDate" to "2026-01-01"
                )

                // When & Then
                mockMvc.post("/api/v1/admin/budgets") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("날짜 형식이 잘못되면 400 에러를 반환한다")
            fun `should return 400 when date format is invalid`() {
                // Given
                val request = mapOf(
                    "startDate" to "2026/01/01",
                    "endDate" to "2026/01/31"
                )

                // When & Then
                mockMvc.post("/api/v1/admin/budgets") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }

    @Nested
    @DisplayName("GET /api/v1/admin/budgets")
    inner class GetDailyBudgetsApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("날짜 범위로 예산을 조회하고 페이징 응답을 반환한다")
            fun `should return paginated budgets by date range`() {
                // Given
                for (i in 1..10) {
                    dailyBudgetRepository.save(
                        DailyBudget(LocalDate.of(2026, 1, i), 100_000, 100_000 - (i * 1000))
                    )
                }

                // When & Then
                mockMvc.get("/api/v1/admin/budgets") {
                    param("startDate", "2026-01-01")
                    param("endDate", "2026-01-10")
                    param("page", "0")
                    param("size", "5")
                    param("sort", "budgetDate,desc")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(5) }
                    jsonPath("$.data.totalElements") { value(10) }
                    jsonPath("$.data.totalPages") { value(2) }
                    jsonPath("$.data.currentPage") { value(0) }
                    jsonPath("$.data.pageSize") { value(5) }
                    jsonPath("$.data.hasNext") { value(true) }
                    jsonPath("$.data.hasPrevious") { value(false) }
                    // 내림차순 정렬 확인
                    jsonPath("$.data.content[0].budgetDate") { value("2026-01-10") }
                    jsonPath("$.data.content[4].budgetDate") { value("2026-01-06") }
                }
            }

            @Test
            @DisplayName("기본 파라미터로 조회할 수 있다")
            fun `should return budgets with default parameters`() {
                // Given
                for (i in 1..3) {
                    dailyBudgetRepository.save(
                        DailyBudget(LocalDate.of(2026, 2, i), 100_000, 100_000)
                    )
                }

                // When & Then
                mockMvc.get("/api/v1/admin/budgets") {
                    param("startDate", "2026-02-01")
                    param("endDate", "2026-02-28")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(3) }
                    jsonPath("$.data.pageSize") { value(10) }
                    jsonPath("$.data.currentPage") { value(0) }
                }
            }

            @Test
            @DisplayName("오름차순 정렬로 조회할 수 있다")
            fun `should return budgets sorted in ascending order`() {
                // Given
                dailyBudgetRepository.save(
                    DailyBudget(LocalDate.of(2026, 3, 15), 100_000, 100_000)
                )
                dailyBudgetRepository.save(
                    DailyBudget(LocalDate.of(2026, 3, 10), 100_000, 100_000)
                )
                dailyBudgetRepository.save(
                    DailyBudget(LocalDate.of(2026, 3, 20), 100_000, 100_000)
                )

                // When & Then
                mockMvc.get("/api/v1/admin/budgets") {
                    param("startDate", "2026-03-01")
                    param("endDate", "2026-03-31")
                    param("sort", "budgetDate,asc")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content[0].budgetDate") { value("2026-03-10") }
                    jsonPath("$.data.content[1].budgetDate") { value("2026-03-15") }
                    jsonPath("$.data.content[2].budgetDate") { value("2026-03-20") }
                }
            }

            @Test
            @DisplayName("조회 범위 내 예산이 없으면 빈 페이징 응답을 반환한다")
            fun `should return empty pagination response when no budgets in range`() {
                // Given
                dailyBudgetRepository.save(
                    DailyBudget(LocalDate.of(2026, 1, 15), 100_000, 100_000)
                )

                // When & Then
                mockMvc.get("/api/v1/admin/budgets") {
                    param("startDate", "2026-02-01")
                    param("endDate", "2026-02-28")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(0) }
                    jsonPath("$.data.totalElements") { value(0) }
                    jsonPath("$.data.totalPages") { value(0) }
                }
            }

            @Test
            @DisplayName("두 번째 페이지를 조회할 수 있다")
            fun `should return second page of budgets`() {
                // Given
                for (i in 1..15) {
                    dailyBudgetRepository.save(
                        DailyBudget(LocalDate.of(2026, 4, i), 100_000, 100_000)
                    )
                }

                // When & Then
                mockMvc.get("/api/v1/admin/budgets") {
                    param("startDate", "2026-04-01")
                    param("endDate", "2026-04-30")
                    param("page", "1")
                    param("size", "10")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(5) }
                    jsonPath("$.data.totalElements") { value(15) }
                    jsonPath("$.data.currentPage") { value(1) }
                    jsonPath("$.data.hasNext") { value(false) }
                    jsonPath("$.data.hasPrevious") { value(true) }
                }
            }
        }

        @Nested
        @DisplayName("실패 케이스 - 유효성 검증")
        inner class ValidationFailTest {

            @Test
            @DisplayName("시작일이 없으면 400 에러를 반환한다")
            fun `should return 400 when startDate is missing`() {
                // When & Then
                mockMvc.get("/api/v1/admin/budgets") {
                    param("endDate", "2026-01-31")
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("종료일이 없으면 400 에러를 반환한다")
            fun `should return 400 when endDate is missing`() {
                // When & Then
                mockMvc.get("/api/v1/admin/budgets") {
                    param("startDate", "2026-01-01")
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("페이지 번호가 음수면 400 에러를 반환한다")
            fun `should return 400 when page is negative`() {
                // When & Then
                mockMvc.get("/api/v1/admin/budgets") {
                    param("startDate", "2026-01-01")
                    param("endDate", "2026-01-31")
                    param("page", "-1")
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("페이지 크기가 0 이하면 400 에러를 반환한다")
            fun `should return 400 when size is zero or negative`() {
                // When & Then
                mockMvc.get("/api/v1/admin/budgets") {
                    param("startDate", "2026-01-01")
                    param("endDate", "2026-01-31")
                    param("size", "0")
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("페이지 크기가 100을 초과하면 400 에러를 반환한다")
            fun `should return 400 when size exceeds 100`() {
                // When & Then
                mockMvc.get("/api/v1/admin/budgets") {
                    param("startDate", "2026-01-01")
                    param("endDate", "2026-01-31")
                    param("size", "101")
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("날짜 형식이 잘못되면 400 에러를 반환한다")
            fun `should return 400 when date format is invalid`() {
                // When & Then
                mockMvc.get("/api/v1/admin/budgets") {
                    param("startDate", "2026/01/01")
                    param("endDate", "2026/01/31")
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }
}
