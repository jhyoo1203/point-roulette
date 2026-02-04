package com.pointroulette.application.budget

import com.pointroulette.application.budget.dto.DailyBudgetCreateRequest
import com.pointroulette.application.budget.dto.DailyBudgetSearchRequest
import com.pointroulette.domain.budget.DailyBudget
import com.pointroulette.domain.budget.DailyBudgetRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.anyIterable
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.data.domain.PageImpl
import java.time.LocalDate

/**
 * DailyBudgetService 단위 테스트
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("DailyBudgetService 테스트")
class DailyBudgetServiceTest {

    @Mock
    private lateinit var dailyBudgetRepository: DailyBudgetRepository

    @InjectMocks
    private lateinit var dailyBudgetService: DailyBudgetService

    @Nested
    @DisplayName("createDailyBudgets 메서드")
    inner class CreateDailyBudgetsTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("날짜 범위 내 예산을 생성하고 생성된 예산 목록을 반환한다")
            fun `should create daily budgets and return created budget list`() {
                // Given
                val startDate = LocalDate.of(2026, 1, 1)
                val endDate = LocalDate.of(2026, 1, 3)
                val request = DailyBudgetCreateRequest(startDate, endDate)

                given(dailyBudgetRepository.existsByBudgetDate(any())).willReturn(false)
                given(dailyBudgetRepository.saveAll(anyIterable())).willAnswer { invocation ->
                    val budgets = invocation.getArgument<List<DailyBudget>>(0)
                    budgets
                }

                // When
                val response = dailyBudgetService.createDailyBudgets(request)

                // Then
                assertThat(response).hasSize(3)
                assertThat(response[0].budgetDate).isEqualTo(LocalDate.of(2026, 1, 1))
                assertThat(response[1].budgetDate).isEqualTo(LocalDate.of(2026, 1, 2))
                assertThat(response[2].budgetDate).isEqualTo(LocalDate.of(2026, 1, 3))
                assertThat(response[0].totalAmount).isEqualTo(100_000)
                assertThat(response[0].remainingAmount).isEqualTo(100_000)

                verify(dailyBudgetRepository).existsByBudgetDate(LocalDate.of(2026, 1, 1))
                verify(dailyBudgetRepository).existsByBudgetDate(LocalDate.of(2026, 1, 2))
                verify(dailyBudgetRepository).existsByBudgetDate(LocalDate.of(2026, 1, 3))
                verify(dailyBudgetRepository).saveAll(anyIterable())
            }

            @Test
            @DisplayName("단일 날짜 예산을 생성할 수 있다")
            fun `should create single day budget`() {
                // Given
                val targetDate = LocalDate.of(2026, 1, 15)
                val request = DailyBudgetCreateRequest(targetDate, targetDate)

                given(dailyBudgetRepository.existsByBudgetDate(targetDate)).willReturn(false)
                given(dailyBudgetRepository.saveAll(anyIterable())).willAnswer { invocation ->
                    invocation.getArgument<List<DailyBudget>>(0)
                }

                // When
                val response = dailyBudgetService.createDailyBudgets(request)

                // Then
                assertThat(response).hasSize(1)
                assertThat(response[0].budgetDate).isEqualTo(targetDate)
                verify(dailyBudgetRepository).existsByBudgetDate(targetDate)
            }

            @Test
            @DisplayName("이미 존재하는 날짜는 건너뛰고 새로운 날짜만 생성한다")
            fun `should skip existing dates and create only new dates`() {
                // Given
                val startDate = LocalDate.of(2026, 1, 1)
                val endDate = LocalDate.of(2026, 1, 5)
                val request = DailyBudgetCreateRequest(startDate, endDate)

                // 1월 2일과 4일만 이미 존재
                given(dailyBudgetRepository.existsByBudgetDate(LocalDate.of(2026, 1, 1))).willReturn(false)
                given(dailyBudgetRepository.existsByBudgetDate(LocalDate.of(2026, 1, 2))).willReturn(true)
                given(dailyBudgetRepository.existsByBudgetDate(LocalDate.of(2026, 1, 3))).willReturn(false)
                given(dailyBudgetRepository.existsByBudgetDate(LocalDate.of(2026, 1, 4))).willReturn(true)
                given(dailyBudgetRepository.existsByBudgetDate(LocalDate.of(2026, 1, 5))).willReturn(false)
                given(dailyBudgetRepository.saveAll(anyIterable())).willAnswer { invocation ->
                    invocation.getArgument<List<DailyBudget>>(0)
                }

                // When
                val response = dailyBudgetService.createDailyBudgets(request)

                // Then
                assertThat(response).hasSize(3)
                assertThat(response.map { it.budgetDate }).containsExactly(
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 1, 3),
                    LocalDate.of(2026, 1, 5)
                )
                verify(dailyBudgetRepository).saveAll(anyIterable())
            }

            @Test
            @DisplayName("모든 날짜가 이미 존재하면 빈 목록을 반환한다")
            fun `should return empty list when all dates already exist`() {
                // Given
                val startDate = LocalDate.of(2026, 1, 1)
                val endDate = LocalDate.of(2026, 1, 2)
                val request = DailyBudgetCreateRequest(startDate, endDate)

                given(dailyBudgetRepository.existsByBudgetDate(any())).willReturn(true)
                given(dailyBudgetRepository.saveAll(anyIterable())).willReturn(emptyList())

                // When
                val response = dailyBudgetService.createDailyBudgets(request)

                // Then
                assertThat(response).isEmpty()
                verify(dailyBudgetRepository).saveAll(emptyList())
            }
        }
    }

    @Nested
    @DisplayName("getDailyBudgets 메서드")
    inner class GetDailyBudgetsTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("날짜 범위로 예산을 조회하고 페이징 응답을 반환한다")
            fun `should return paginated daily budgets by date range`() {
                // Given
                val startDate = LocalDate.of(2026, 1, 1)
                val endDate = LocalDate.of(2026, 1, 10)
                val searchRequest = DailyBudgetSearchRequest(
                    startDate = startDate,
                    endDate = endDate,
                    page = 0,
                    size = 10,
                    sort = "budgetDate,desc"
                )

                val budgets = listOf(
                    DailyBudget(LocalDate.of(2026, 1, 5), 100_000, 100_000),
                    DailyBudget(LocalDate.of(2026, 1, 4), 100_000, 80_000),
                    DailyBudget(LocalDate.of(2026, 1, 3), 100_000, 50_000)
                )
                val page = PageImpl(budgets)

                given(dailyBudgetRepository.findByBudgetDateBetween(any(), any(), any())).willReturn(page)

                // When
                val response = dailyBudgetService.getDailyBudgets(searchRequest)

                // Then
                assertThat(response.content).hasSize(3)
                assertThat(response.totalElements).isEqualTo(3)
                assertThat(response.currentPage).isEqualTo(0)
                assertThat(response.pageSize).isEqualTo(3)
                assertThat(response.content[0].budgetDate).isEqualTo(LocalDate.of(2026, 1, 5))
                assertThat(response.content[1].budgetDate).isEqualTo(LocalDate.of(2026, 1, 4))
                assertThat(response.content[2].budgetDate).isEqualTo(LocalDate.of(2026, 1, 3))

                verify(dailyBudgetRepository).findByBudgetDateBetween(any(), any(), any())
            }

            @Test
            @DisplayName("조회 결과가 없으면 빈 페이징 응답을 반환한다")
            fun `should return empty pagination response when no budgets found`() {
                // Given
                val startDate = LocalDate.of(2026, 2, 1)
                val endDate = LocalDate.of(2026, 2, 28)
                val searchRequest = DailyBudgetSearchRequest(
                    startDate = startDate,
                    endDate = endDate
                )

                val emptyPage = PageImpl<DailyBudget>(emptyList())
                given(dailyBudgetRepository.findByBudgetDateBetween(any(), any(), any())).willReturn(emptyPage)

                // When
                val response = dailyBudgetService.getDailyBudgets(searchRequest)

                // Then
                assertThat(response.content).isEmpty()
                assertThat(response.totalElements).isEqualTo(0)
                assertThat(response.totalPages).isEqualTo(1)
                verify(dailyBudgetRepository).findByBudgetDateBetween(any(), any(), any())
            }

            @Test
            @DisplayName("페이징 파라미터를 올바르게 적용한다")
            fun `should apply pagination parameters correctly`() {
                // Given
                val searchRequest = DailyBudgetSearchRequest(
                    startDate = LocalDate.of(2026, 1, 1),
                    endDate = LocalDate.of(2026, 1, 31),
                    page = 1,
                    size = 5,
                    sort = "budgetDate,asc"
                )

                val emptyPage = PageImpl<DailyBudget>(emptyList())
                given(dailyBudgetRepository.findByBudgetDateBetween(any(), any(), any())).willReturn(emptyPage)

                // When
                dailyBudgetService.getDailyBudgets(searchRequest)

                // Then
                verify(dailyBudgetRepository).findByBudgetDateBetween(any(), any(), any())
            }
        }
    }
}
