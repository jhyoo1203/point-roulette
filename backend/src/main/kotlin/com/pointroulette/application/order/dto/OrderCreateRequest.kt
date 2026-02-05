package com.pointroulette.application.order.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

/**
 * 주문 생성 요청 DTO
 */
@Schema(description = "주문 생성 요청")
data class OrderCreateRequest(
    @field:Min(1, message = "상품 ID는 1 이상이어야 합니다")
    @field:Schema(
        description = "상품 ID",
        example = "1",
        minimum = "1"
    )
    val productId: Long,

    @field:Min(1, message = "수량은 1 이상이어야 합니다")
    @field:Schema(
        description = "주문 수량",
        example = "2",
        minimum = "1"
    )
    val quantity: Int
)
