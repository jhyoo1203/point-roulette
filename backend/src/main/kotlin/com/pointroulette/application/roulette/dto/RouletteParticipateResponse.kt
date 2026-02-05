package com.pointroulette.application.roulette.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RouletteParticipateResponse(
    @field:Schema(description = "성공 여부 (당첨: true, 예산 부족: false)", example = "true")
    val success: Boolean,

    @field:Schema(description = "당첨 포인트 (예산 부족 시 null)", example = "500")
    val wonAmount: Int?,

    @field:Schema(description = "오늘 남은 예산", example = "95000")
    val remainingBudget: Int
)
