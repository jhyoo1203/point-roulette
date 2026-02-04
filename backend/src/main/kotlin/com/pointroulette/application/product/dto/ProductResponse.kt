package com.pointroulette.application.product.dto

import com.pointroulette.domain.product.Product
import com.pointroulette.domain.product.ProductStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "상품 응답")
data class ProductResponse(
    @field:Schema(description = "상품 ID", example = "1")
    val id: Long,

    @field:Schema(description = "상품명", example = "포인트 10,000원")
    val name: String,

    @field:Schema(description = "가격", example = "10000")
    val price: Int,

    @field:Schema(description = "재고", example = "100")
    val stock: Int,

    @field:Schema(description = "상품 설명", example = "10,000 포인트 상품")
    val description: String?,

    @field:Schema(description = "상품 상태", example = "ACTIVE")
    val status: ProductStatus,

    @field:Schema(description = "생성일시", example = "2026-01-01T00:00:00")
    val createdAt: LocalDateTime,

    @field:Schema(description = "수정일시", example = "2026-01-01T00:00:00")
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(product: Product): ProductResponse {
            return ProductResponse(
                id = product.id,
                name = product.name,
                price = product.price,
                stock = product.stock,
                description = product.description,
                status = product.status,
                createdAt = product.createdAt,
                updatedAt = product.updatedAt
            )
        }
    }
}
