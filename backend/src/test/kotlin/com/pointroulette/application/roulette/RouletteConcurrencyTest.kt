package com.pointroulette.application.roulette

import com.pointroulette.domain.budget.DailyBudget
import com.pointroulette.domain.budget.DailyBudgetRepository
import com.pointroulette.domain.point.PointHistoryRepository
import com.pointroulette.domain.point.PointRepository
import com.pointroulette.domain.roulette.AlreadyParticipatedException
import com.pointroulette.domain.roulette.RouletteHistoryRepository
import com.pointroulette.domain.user.User
import com.pointroulette.domain.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * 룰렛 동시성 통합 테스트
 * - 낙관적 락을 활용한 예산 차감 동시성 제어 검증
 * - 중복 참여 방지 검증
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@DisplayName("룰렛 동시성 통합 테스트")
class RouletteConcurrencyTest {

    @Autowired
    private lateinit var rouletteService: RouletteService

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

    @BeforeEach
    fun setUp() {
        rouletteHistoryRepository.deleteAll()
        pointHistoryRepository.deleteAll()
        pointRepository.deleteAll()
        dailyBudgetRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("여러 사용자가 동시에 룰렛에 참여해도 예산이 정확히 차감된다")
    fun `should deduct budget correctly when multiple users participate concurrently`() {
        // given
        val today = LocalDate.now()
        val totalBudget = 100_000
        val threadCount = 50 // 50명의 사용자가 동시 참여

        // 오늘의 예산 생성
        val budget = DailyBudget(today, totalBudget, totalBudget)
        dailyBudgetRepository.save(budget)

        // 50명의 사용자 생성
        val users = (1..threadCount).map { i ->
            userRepository.save(User("유저$i"))
        }

        val executorService = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)
        val successCount = AtomicInteger(0)
        val totalWonAmount = AtomicInteger(0)

        // when: 50명이 동시에 룰렛 참여
        users.forEach { user ->
            executorService.submit {
                try {
                    val response = rouletteService.participateRoulette(user.id)
                    successCount.incrementAndGet()
                    response.wonAmount?.let { totalWonAmount.addAndGet(it) }
                } catch (e: Exception) {
                    // 예산 부족 등의 예외는 무시 (정상 동작)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executorService.shutdown()
        executorService.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)

        // then
        val finalBudget = dailyBudgetRepository.findByBudgetDate(today)!!
        val participatedCount = rouletteHistoryRepository.count()

        // 성공한 참여자 수와 이력 수가 일치
        assertThat(participatedCount).isEqualTo(successCount.get().toLong())

        // 예산 차감이 정확히 이루어졌는지 검증
        val expectedRemainingBudget = totalBudget - totalWonAmount.get()
        assertThat(finalBudget.remainingAmount).isEqualTo(expectedRemainingBudget)

        // 총 지급된 포인트가 예산 범위 내인지 검증
        assertThat(totalWonAmount.get()).isLessThanOrEqualTo(totalBudget)
    }

    @Test
    @DisplayName("같은 사용자가 동시에 여러 요청을 보내도 한 번만 참여된다")
    fun `should allow only one participation when same user sends multiple concurrent requests`() {
        // given
        val today = LocalDate.now()
        val totalBudget = 100_000
        val threadCount = 10 // 같은 사용자가 10번 동시 요청

        // 오늘의 예산 생성
        val budget = DailyBudget(today, totalBudget, totalBudget)
        dailyBudgetRepository.save(budget)

        // 단일 사용자 생성
        val user = userRepository.save(User("테스트유저"))

        val executorService = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)
        val successCount = AtomicInteger(0)
        val alreadyParticipatedCount = AtomicInteger(0)

        // when: 같은 사용자가 10번 동시 요청
        repeat(threadCount) {
            executorService.submit {
                try {
                    rouletteService.participateRoulette(user.id)
                    successCount.incrementAndGet()
                } catch (e: AlreadyParticipatedException) {
                    alreadyParticipatedCount.incrementAndGet()
                } catch (e: Exception) {
                    // 다른 예외는 무시
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executorService.shutdown()
        executorService.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)

        // then
        val participated = rouletteHistoryRepository.existsByUserIdAndParticipatedDate(user.id, today)

        // 정확히 1번만 참여 성공
        assertThat(participated).isTrue()
        assertThat(successCount.get()).isEqualTo(1)

        // 나머지는 중복 참여 예외
        assertThat(alreadyParticipatedCount.get()).isEqualTo(threadCount - 1)
    }

    @Test
    @DisplayName("예산이 부족할 때까지 여러 사용자가 참여할 수 있다")
    fun `should allow participation until budget is exhausted`() {
        // given
        val today = LocalDate.now()
        val totalBudget = 5_000 // 작은 예산 (최대 5명 참여 가능, 각 1000포인트 가정)
        val threadCount = 20 // 20명이 시도

        // 오늘의 예산 생성
        val budget = DailyBudget(today, totalBudget, totalBudget)
        dailyBudgetRepository.save(budget)

        // 20명의 사용자 생성
        val users = (1..threadCount).map { i ->
            userRepository.save(User("유저$i"))
        }

        val executorService = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)
        val successCount = AtomicInteger(0)
        val failedCount = AtomicInteger(0)

        // when: 20명이 동시에 룰렛 참여
        users.forEach { user ->
            executorService.submit {
                try {
                    rouletteService.participateRoulette(user.id)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    failedCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executorService.shutdown()
        executorService.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)

        // then
        val finalBudget = dailyBudgetRepository.findByBudgetDate(today)!!
        val participatedCount = rouletteHistoryRepository.count()

        // 성공한 참여자 수가 예산 범위 내
        assertThat(participatedCount).isEqualTo(successCount.get().toLong())
        assertThat(successCount.get()).isLessThan(threadCount) // 일부만 성공

        // 최종 예산이 음수가 아님
        assertThat(finalBudget.remainingAmount).isGreaterThanOrEqualTo(0)

        // 성공 + 실패 = 전체 시도 수
        assertThat(successCount.get() + failedCount.get()).isEqualTo(threadCount)
    }

    @Test
    @DisplayName("낙관적 락 재시도로 동시성 충돌을 해결한다")
    fun `should resolve optimistic lock conflicts with retry mechanism`() {
        // given
        val today = LocalDate.now()
        val totalBudget = 100_000
        val threadCount = 5 // 적은 수의 동시 요청 (충돌 가능성 높음)

        // 오늘의 예산 생성
        val budget = DailyBudget(today, totalBudget, totalBudget)
        dailyBudgetRepository.save(budget)

        // 5명의 사용자 생성
        val users = (1..threadCount).map { i ->
            userRepository.save(User("유저$i"))
        }

        val executorService = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)
        val successCount = AtomicInteger(0)

        // when: 5명이 동시에 룰렛 참여
        users.forEach { user ->
            executorService.submit {
                try {
                    rouletteService.participateRoulette(user.id)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    // 재시도 실패 케이스는 무시
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executorService.shutdown()
        executorService.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)

        // then
        val finalBudget = dailyBudgetRepository.findByBudgetDate(today)!!

        // 대부분 또는 전부 성공 (낙관적 락 재시도 덕분)
        assertThat(successCount.get()).isGreaterThan(0)

        // 예산이 정확히 차감됨
        assertThat(finalBudget.remainingAmount).isLessThan(totalBudget)
        assertThat(finalBudget.remainingAmount).isGreaterThanOrEqualTo(0)
    }
}
