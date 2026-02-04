package com.pointroulette.application.budget.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@Schema(description = "일일 예산 생성 요청")
data class DailyBudgetCreateRequest(
    @field:NotNull(message = "시작일은 필수입니다")
    @field:Schema(description = "시작일", example = "2026-01-01", required = true)
    val startDate: LocalDate,

    @field:NotNull(message = "종료일은 필수입니다")
    @field:Schema(description = "종료일", example = "2026-12-31", required = true)
    val endDate: LocalDate
) {
    init {
        require(startDate <= endDate) { "시작일은 종료일보다 이전이어야 합니다" }
    }
}
