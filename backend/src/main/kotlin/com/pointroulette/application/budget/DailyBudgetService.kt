package com.pointroulette.application.budget

import com.pointroulette.application.budget.dto.DailyBudgetCreateRequest
import com.pointroulette.application.budget.dto.DailyBudgetResponse
import com.pointroulette.application.budget.dto.DailyBudgetSearchRequest
import com.pointroulette.common.model.PaginationResponse
import com.pointroulette.domain.budget.DailyBudget
import com.pointroulette.domain.budget.DailyBudgetRepository
import com.pointroulette.domain.roulette.RouletteParticipationException
import com.pointroulette.presentation.exception.BusinessException
import com.pointroulette.presentation.exception.ErrorCode
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class DailyBudgetService(
    private val dailyBudgetRepository: DailyBudgetRepository
) {

    companion object {
        private const val BUDGET_AMOUNT = 100_000
    }

    /**
     * 날짜 범위로 일일 예산 생성 (JPA Batch Insert)
     * - 이미 존재하는 날짜는 건너뜀
     * - application.yaml의 batch_size 설정(50) 활용
     */
    @Transactional
    fun createDailyBudgets(request: DailyBudgetCreateRequest): List<DailyBudgetResponse> {
        val budgetsToCreate = mutableListOf<DailyBudget>()
        var currentDate = request.startDate

        // 생성할 예산 목록 수집 (이미 존재하는 날짜는 제외)
        while (!currentDate.isAfter(request.endDate)) {
            if (!dailyBudgetRepository.existsByBudgetDate(currentDate)) {
                val dailyBudget = DailyBudget(currentDate, BUDGET_AMOUNT, BUDGET_AMOUNT)
                budgetsToCreate.add(dailyBudget)
            }
            currentDate = currentDate.plusDays(1)
        }

        val savedBudgets = dailyBudgetRepository.saveAll(budgetsToCreate)
        return savedBudgets.map { DailyBudgetResponse.from(it) }
    }

    /**
     * 날짜 범위로 일일 예산 조회
     */
    @Transactional(readOnly = true)
    fun getDailyBudgets(searchRequest: DailyBudgetSearchRequest): PaginationResponse<DailyBudgetResponse> {
        val pageable = searchRequest.toPageable()
        val page = dailyBudgetRepository.findByBudgetDateBetween(
            searchRequest.startDate,
            searchRequest.endDate,
            pageable
        )

        return PaginationResponse.from(page, DailyBudgetResponse::from)
    }

    /**
     * 예산을 차감합니다.
     * 낙관적 락 실패 시 최대 3회 자동 재시도합니다. (delay=50ms, multiplier=2.0, maxDelay=200ms)
     *
     * @param budget 차감할 DailyBudget 엔티티
     * @param amount 차감할 금액
     * @return true: 차감 성공
     * @throws RouletteParticipationException 예산 부족 시
     */
    @Transactional
    @Retryable(
        retryFor = [ObjectOptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 50, multiplier = 2.0, maxDelay = 200)
    )
    fun deductBudget(budget: DailyBudget, amount: Int): Boolean {
        if (budget.remainingAmount < amount) {
            throw RouletteParticipationException("예산이 부족합니다.")
        }

        budget.remainingAmount -= amount
        dailyBudgetRepository.save(budget)
        return true
    }

    /**
     * 오늘의 예산을 조회합니다 (읽기 전용).
     * @return 오늘의 DailyBudget
     * @throws BusinessException 오늘의 예산이 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    fun getTodayBudget(): DailyBudget {
        return dailyBudgetRepository.findByBudgetDate(LocalDate.now())
            ?: throw BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "오늘의 예산이 존재하지 않습니다.")
    }

    /**
     * 예산을 복구합니다 (룰렛 참여 취소 시).
     * 낙관적 락 실패 시 최대 3회 자동 재시도합니다.
     *
     * @param budget 복구할 DailyBudget 엔티티
     * @param amount 복구할 금액
     */
    @Transactional
    @Retryable(
        retryFor = [ObjectOptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 50, multiplier = 2.0, maxDelay = 200)
    )
    fun refundBudget(budget: DailyBudget, amount: Int) {
        budget.remainingAmount += amount
        dailyBudgetRepository.save(budget)
    }
}
