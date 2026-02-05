package com.pointroulette.application.order.dto

import com.pointroulette.domain.order.Order
import com.pointroulette.domain.order.OrderStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 주문 응답 DTO
 */
@Schema(description = "주문 응답")
data class OrderResponse(
    @field:Schema(description = "주문 ID", example = "1")
    val id: Long,

    @field:Schema(description = "사용자 ID", example = "1")
    val userId: Long,

    @field:Schema(description = "상품 ID", example = "1")
    val productId: Long,

    @field:Schema(description = "상품명", example = "포인트 10,000원")
    val productName: String,

    @field:Schema(description = "주문 수량", example = "2")
    val quantity: Int,

    @field:Schema(description = "총 가격", example = "20000")
    val totalPrice: Int,

    @field:Schema(description = "주문 상태", example = "COMPLETED")
    val status: OrderStatus,

    @field:Schema(description = "주문 생성 시각", example = "2026-01-01T12:00:00")
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(order: Order): OrderResponse {
            return OrderResponse(
                id = order.id,
                userId = order.user.id,
                productId = order.product.id,
                productName = order.product.name,
                quantity = order.quantity,
                totalPrice = order.totalPrice,
                status = order.status,
                createdAt = order.createdAt
            )
        }
    }
}
