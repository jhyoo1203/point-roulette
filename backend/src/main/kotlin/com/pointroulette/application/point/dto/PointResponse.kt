package com.pointroulette.application.point.dto

import com.pointroulette.domain.point.Point
import com.pointroulette.domain.point.PointSourceType
import com.pointroulette.domain.point.PointStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 포인트 응답 DTO
 */
@Schema(description = "포인트 응답")
data class PointResponse(
    @field:Schema(description = "포인트 ID", example = "1")
    val id: Long,

    @field:Schema(description = "초기 금액", example = "500")
    val initialAmount: Int,

    @field:Schema(description = "잔여 금액", example = "500")
    val remainingAmount: Int,

    @field:Schema(description = "만료일시", example = "2026-02-05T12:00:00")
    val expiresAt: LocalDateTime,

    @field:Schema(description = "포인트 획득 경로", example = "ROULETTE")
    val sourceType: PointSourceType,

    @field:Schema(description = "포인트 획득 참조 ID", example = "1")
    val sourceId: Long,

    @field:Schema(description = "포인트 상태", example = "ACTIVE")
    val status: PointStatus,

    @field:Schema(description = "생성일시", example = "2026-01-06T12:00:00")
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(point: Point): PointResponse {
            return PointResponse(
                id = point.id,
                initialAmount = point.initialAmount,
                remainingAmount = point.remainingAmount,
                expiresAt = point.expiresAt,
                sourceType = point.sourceType,
                sourceId = point.sourceId,
                status = point.status,
                createdAt = point.createdAt
            )
        }
    }
}
