package com.pointroulette.application.budget.dto

import com.pointroulette.domain.budget.DailyBudget
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

@Schema(description = "일일 예산 응답")
data class DailyBudgetResponse(
    @field:Schema(description = "예산 ID", example = "1")
    val id: Long,

    @field:Schema(description = "예산 날짜", example = "2026-01-01")
    val budgetDate: LocalDate,

    @field:Schema(description = "총 예산 금액", example = "100000")
    val totalAmount: Int,

    @field:Schema(description = "잔여 예산 금액", example = "50000")
    val remainingAmount: Int,

    @field:Schema(description = "생성일시", example = "2026-01-01T00:00:00")
    val createdAt: LocalDateTime,

    @field:Schema(description = "수정일시", example = "2026-01-01T00:00:00")
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(dailyBudget: DailyBudget): DailyBudgetResponse {
            return DailyBudgetResponse(
                id = dailyBudget.id,
                budgetDate = dailyBudget.budgetDate,
                totalAmount = dailyBudget.totalAmount,
                remainingAmount = dailyBudget.remainingAmount,
                createdAt = dailyBudget.createdAt,
                updatedAt = dailyBudget.updatedAt
            )
        }
    }
}
