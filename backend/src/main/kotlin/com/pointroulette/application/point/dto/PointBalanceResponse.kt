package com.pointroulette.application.point.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 포인트 잔액 응답 DTO
 */
@Schema(description = "포인트 잔액 응답")
data class PointBalanceResponse(
    @field:Schema(description = "사용자 ID", example = "1")
    val userId: Long,

    @field:Schema(description = "현재 포인트 잔액", example = "5000")
    val currentPoint: Int,

    @field:Schema(description = "7일 이내 만료 예정 포인트", example = "1000")
    val expiringPointIn7Days: Int,

    @field:Schema(description = "보유한 포인트 목록")
    val points: List<PointResponse>
)
