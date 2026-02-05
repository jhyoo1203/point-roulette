package com.pointroulette.application.roulette.dto

import io.swagger.v3.oas.annotations.media.Schema

data class RouletteStatusResponse(
    @field:Schema(description = "오늘 참여 여부", example = "false")
    val hasParticipatedToday: Boolean,

    @field:Schema(description = "오늘 남은 예산", example = "100000")
    val todayRemainingBudget: Int,

    @field:Schema(description = "마지막 참여 이력")
    val lastParticipation: RouletteHistoryResponse?
)
