package com.pointroulette.application.roulette

import com.pointroulette.application.budget.DailyBudgetService
import com.pointroulette.application.point.PointService
import com.pointroulette.application.user.UserService
import com.pointroulette.domain.budget.DailyBudget
import com.pointroulette.domain.point.PointSourceType
import com.pointroulette.domain.roulette.AlreadyParticipatedException
import com.pointroulette.domain.roulette.RouletteHistory
import com.pointroulette.domain.roulette.RouletteHistoryRepository
import com.pointroulette.domain.roulette.RouletteStatus
import com.pointroulette.domain.user.User
import com.pointroulette.infrastructure.util.RandomPointGenerator
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
import org.springframework.dao.DataIntegrityViolationException
import java.time.LocalDate

/**
 * RouletteService 단위 테스트
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("RouletteService 테스트")
class RouletteServiceTest {

    @Mock
    private lateinit var rouletteHistoryRepository: RouletteHistoryRepository

    @Mock
    private lateinit var dailyBudgetService: DailyBudgetService

    @Mock
    private lateinit var pointService: PointService

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var randomPointGenerator: RandomPointGenerator

    @InjectMocks
    private lateinit var rouletteService: RouletteService

    @Nested
    @DisplayName("participateRoulette 메서드")
    inner class ParticipateRouletteTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("정상적으로 룰렛에 참여하고 포인트를 획득한다")
            fun `should participate in roulette and earn points successfully`() {
                // given
                val userId = 1L
                val today = LocalDate.now()
                val randomPoint = 500

                val user = User("테스트유저")
                val budget = DailyBudget(today, 100_000, 100_000)
                val savedHistory = RouletteHistory(user, today, randomPoint, budget, RouletteStatus.SUCCESS)

                given(rouletteHistoryRepository.existsByUserIdAndParticipatedDate(userId, today))
                    .willReturn(false)
                given(userService.getUser(userId)).willReturn(user)
                given(dailyBudgetService.getTodayBudget()).willReturn(budget)
                given(randomPointGenerator.generate()).willReturn(randomPoint)
                given(dailyBudgetService.deductBudget(budget, randomPoint)).willAnswer {
                    budget.remainingAmount -= randomPoint
                    true
                }
                given(rouletteHistoryRepository.save(any())).willReturn(savedHistory)

                // when
                val response = rouletteService.participateRoulette(userId)

                // then
                assertThat(response.success).isTrue()
                assertThat(response.wonAmount).isEqualTo(randomPoint)
                assertThat(response.remainingBudget).isEqualTo(99_500)

                verify(rouletteHistoryRepository).existsByUserIdAndParticipatedDate(userId, today)
                verify(userService).getUser(userId)
                verify(dailyBudgetService).getTodayBudget()
                verify(randomPointGenerator).generate()
                verify(dailyBudgetService).deductBudget(budget, randomPoint)
                verify(rouletteHistoryRepository).save(any())
                verify(pointService).earnPoint(
                    eq(userId),
                    eq(randomPoint),
                    eq(PointSourceType.ROULETTE),
                    any()
                )
            }

            @Test
            @DisplayName("최소 포인트(100)를 획득할 수 있다")
            fun `should earn minimum points`() {
                // given
                val userId = 1L
                val today = LocalDate.now()
                val randomPoint = 100

                val user = User("테스트유저")
                val budget = DailyBudget(today, 100_000, 100_000)
                val savedHistory = RouletteHistory(user, today, randomPoint, budget, RouletteStatus.SUCCESS)

                given(rouletteHistoryRepository.existsByUserIdAndParticipatedDate(userId, today))
                    .willReturn(false)
                given(userService.getUser(userId)).willReturn(user)
                given(dailyBudgetService.getTodayBudget()).willReturn(budget)
                given(randomPointGenerator.generate()).willReturn(randomPoint)
                given(dailyBudgetService.deductBudget(budget, randomPoint)).willAnswer {
                    budget.remainingAmount -= randomPoint
                    true
                }
                given(rouletteHistoryRepository.save(any())).willReturn(savedHistory)

                // when
                val response = rouletteService.participateRoulette(userId)

                // then
                assertThat(response.wonAmount).isEqualTo(100)
                verify(randomPointGenerator).generate()
            }

            @Test
            @DisplayName("최대 포인트(1000)를 획득할 수 있다")
            fun `should earn maximum points`() {
                // given
                val userId = 1L
                val today = LocalDate.now()
                val randomPoint = 1000

                val user = User("테스트유저")
                val budget = DailyBudget(today, 100_000, 100_000)
                val savedHistory = RouletteHistory(user, today, randomPoint, budget, RouletteStatus.SUCCESS)

                given(rouletteHistoryRepository.existsByUserIdAndParticipatedDate(userId, today))
                    .willReturn(false)
                given(userService.getUser(userId)).willReturn(user)
                given(dailyBudgetService.getTodayBudget()).willReturn(budget)
                given(randomPointGenerator.generate()).willReturn(randomPoint)
                given(dailyBudgetService.deductBudget(budget, randomPoint)).willAnswer {
                    budget.remainingAmount -= randomPoint
                    true
                }
                given(rouletteHistoryRepository.save(any())).willReturn(savedHistory)

                // when
                val response = rouletteService.participateRoulette(userId)

                // then
                assertThat(response.wonAmount).isEqualTo(1000)
                verify(randomPointGenerator).generate()
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        inner class FailureTest {

            @Test
            @DisplayName("오늘 이미 참여한 경우 AlreadyParticipatedException 예외를 발생시킨다")
            fun `should throw AlreadyParticipatedException when already participated today`() {
                // given
                val userId = 1L
                val today = LocalDate.now()

                given(rouletteHistoryRepository.existsByUserIdAndParticipatedDate(userId, today))
                    .willReturn(true)

                // when & then
                assertThatThrownBy { rouletteService.participateRoulette(userId) }
                    .isInstanceOf(AlreadyParticipatedException::class.java)
                    .hasMessage("오늘 이미 룰렛에 참여했습니다.")

                verify(rouletteHistoryRepository).existsByUserIdAndParticipatedDate(userId, today)
            }

            @Test
            @DisplayName("동시 요청으로 인한 중복 참여 시 AlreadyParticipatedException 예외를 발생시킨다")
            fun `should throw AlreadyParticipatedException when DataIntegrityViolationException occurs`() {
                // given
                val userId = 1L
                val today = LocalDate.now()

                val user = User("테스트유저")
                val budget = DailyBudget(today, 100_000, 100_000)

                given(rouletteHistoryRepository.existsByUserIdAndParticipatedDate(userId, today))
                    .willReturn(false)
                given(userService.getUser(userId)).willReturn(user)
                given(dailyBudgetService.getTodayBudget()).willReturn(budget)
                given(randomPointGenerator.generate()).willReturn(500)
                given(dailyBudgetService.deductBudget(budget, 500)).willAnswer {
                    budget.remainingAmount -= 500
                    true
                }
                given(rouletteHistoryRepository.save(any()))
                    .willThrow(DataIntegrityViolationException("unique constraint violation"))

                // when & then
                assertThatThrownBy { rouletteService.participateRoulette(userId) }
                    .isInstanceOf(AlreadyParticipatedException::class.java)
                    .hasMessageContaining("동시 요청으로 인한 중복 참여가 감지되었습니다")

                verify(rouletteHistoryRepository).save(any())
            }
        }
    }

    @Nested
    @DisplayName("getTodayStatus 메서드")
    inner class GetTodayStatusTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("오늘 참여하지 않은 경우 hasParticipatedToday가 false이다")
            fun `should return hasParticipatedToday false when not participated today`() {
                // given
                val userId = 1L
                val today = LocalDate.now()
                val remainingBudget = 100_000

                val user = User("테스트유저")
                val budget = DailyBudget(today, 100_000, remainingBudget)

                given(userService.getUser(userId)).willReturn(user)
                given(rouletteHistoryRepository.findByUserIdAndParticipatedDate(userId, today))
                    .willReturn(null)
                given(dailyBudgetService.getTodayBudget()).willReturn(budget)

                // when
                val response = rouletteService.getTodayStatus(userId)

                // then
                assertThat(response.hasParticipatedToday).isFalse()
                assertThat(response.todayRemainingBudget).isEqualTo(remainingBudget)
                assertThat(response.lastParticipation).isNull()

                verify(userService).getUser(userId)
                verify(rouletteHistoryRepository).findByUserIdAndParticipatedDate(userId, today)
                verify(dailyBudgetService).getTodayBudget()
            }

            @Test
            @DisplayName("오늘 참여한 경우 hasParticipatedToday가 true이고 참여 정보를 반환한다")
            fun `should return hasParticipatedToday true with participation info when participated today`() {
                // given
                val userId = 1L
                val today = LocalDate.now()
                val wonAmount = 500
                val remainingBudget = 99_500

                val user = User("테스트유저")
                val budget = DailyBudget(today, 100_000, remainingBudget)
                val history = RouletteHistory(user, today, wonAmount, budget, RouletteStatus.SUCCESS)

                given(userService.getUser(userId)).willReturn(user)
                given(rouletteHistoryRepository.findByUserIdAndParticipatedDate(userId, today))
                    .willReturn(history)
                given(dailyBudgetService.getTodayBudget()).willReturn(budget)

                // when
                val response = rouletteService.getTodayStatus(userId)

                // then
                assertThat(response.hasParticipatedToday).isTrue()
                assertThat(response.todayRemainingBudget).isEqualTo(remainingBudget)
                assertThat(response.lastParticipation).isNotNull
                assertThat(response.lastParticipation?.wonAmount).isEqualTo(wonAmount)

                verify(userService).getUser(userId)
                verify(rouletteHistoryRepository).findByUserIdAndParticipatedDate(userId, today)
                verify(dailyBudgetService).getTodayBudget()
            }

            @Test
            @DisplayName("예산이 모두 소진된 경우에도 정상 조회된다")
            fun `should return status even when budget is exhausted`() {
                // given
                val userId = 1L
                val today = LocalDate.now()
                val remainingBudget = 0

                val user = User("테스트유저")
                val budget = DailyBudget(today, 100_000, remainingBudget)

                given(userService.getUser(userId)).willReturn(user)
                given(rouletteHistoryRepository.findByUserIdAndParticipatedDate(userId, today))
                    .willReturn(null)
                given(dailyBudgetService.getTodayBudget()).willReturn(budget)

                // when
                val response = rouletteService.getTodayStatus(userId)

                // then
                assertThat(response.hasParticipatedToday).isFalse()
                assertThat(response.todayRemainingBudget).isEqualTo(0)

                verify(dailyBudgetService).getTodayBudget()
            }
        }
    }
}
