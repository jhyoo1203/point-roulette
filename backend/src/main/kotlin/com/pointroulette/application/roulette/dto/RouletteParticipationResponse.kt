package com.pointroulette.application.roulette.dto

import com.pointroulette.domain.roulette.RouletteHistory
import io.swagger.v3.oas.annotations.media.Schema

/**
 * 룰렛 참여 이력 응답 (관리자용 - 유저 정보 포함)
 */
data class RouletteParticipationResponse(
    @field:Schema(description = "룰렛 이력 ID", example = "1")
    val id: Long,

    @field:Schema(description = "유저 ID", example = "1")
    val userId: Long,

    @field:Schema(description = "유저 닉네임", example = "홍길동")
    val userName: String,

    @field:Schema(description = "참여 날짜", example = "2026-02-06")
    val participatedDate: String,

    @field:Schema(description = "당첨 포인트", example = "500")
    val wonAmount: Int,

    @field:Schema(description = "상태", example = "SUCCESS")
    val status: String
) {
    companion object {
        fun from(history: RouletteHistory): RouletteParticipationResponse {
            return RouletteParticipationResponse(
                id = history.id,
                userId = history.user.id,
                userName = history.user.nickname,
                participatedDate = history.participatedDate.toString(),
                wonAmount = history.wonAmount,
                status = history.status.name
            )
        }
    }
}
