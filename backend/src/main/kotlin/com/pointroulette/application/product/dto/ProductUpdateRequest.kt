package com.pointroulette.application.product.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

@Schema(description = "상품 수정 요청 (Partial Update)")
data class ProductUpdateRequest(
    @field:Size(min = 1, max = 100, message = "상품명은 1자 이상 100자 이하여야 합니다")
    @field:Schema(description = "상품명", example = "포인트 15,000원", required = false)
    val name: String? = null,

    @field:Min(value = 0, message = "가격은 0 이상이어야 합니다")
    @field:Schema(description = "가격", example = "15000", required = false)
    val price: Int? = null,

    @field:Min(value = 0, message = "재고는 0 이상이어야 합니다")
    @field:Schema(description = "재고", example = "150", required = false)
    val stock: Int? = null,

    @field:Size(max = 500, message = "설명은 500자 이하여야 합니다")
    @field:Schema(description = "상품 설명", example = "15,000 포인트 상품", required = false)
    val description: String? = null
)
