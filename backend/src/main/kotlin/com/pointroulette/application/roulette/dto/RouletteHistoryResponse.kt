package com.pointroulette.application.roulette.dto

import com.pointroulette.domain.roulette.RouletteHistory
import io.swagger.v3.oas.annotations.media.Schema

data class RouletteHistoryResponse(
    @field:Schema(description = "룰렛 이력 ID", example = "1")
    val id: Long,

    @field:Schema(description = "참여 날짜", example = "2026-02-05")
    val participatedDate: String,

    @field:Schema(description = "당첨 포인트", example = "500")
    val wonAmount: Int,

    @field:Schema(description = "상태", example = "SUCCESS")
    val status: String
) {
    companion object {
        fun from(history: RouletteHistory): RouletteHistoryResponse {
            return RouletteHistoryResponse(
                id = history.id,
                participatedDate = history.participatedDate.toString(),
                wonAmount = history.wonAmount,
                status = history.status.name
            )
        }
    }
}
