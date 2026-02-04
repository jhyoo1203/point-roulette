package com.pointroulette.application.budget

import com.pointroulette.application.budget.dto.DailyBudgetCreateRequest
import com.pointroulette.application.budget.dto.DailyBudgetResponse
import com.pointroulette.application.budget.dto.DailyBudgetSearchRequest
import com.pointroulette.common.model.PaginationResponse
import com.pointroulette.domain.budget.DailyBudget
import com.pointroulette.domain.budget.DailyBudgetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DailyBudgetService(
    private val dailyBudgetRepository: DailyBudgetRepository
) {
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
                val dailyBudget = DailyBudget(currentDate, 100_000, 100_000)
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
}
