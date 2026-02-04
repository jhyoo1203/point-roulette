package com.pointroulette.application.product.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "상품 생성 요청")
data class ProductCreateRequest(
    @field:NotBlank(message = "상품명은 필수입니다")
    @field:Size(min = 1, max = 100, message = "상품명은 1자 이상 100자 이하여야 합니다")
    @field:Schema(description = "상품명", example = "포인트 10,000원", required = true)
    val name: String,

    @field:Min(value = 0, message = "가격은 0 이상이어야 합니다")
    @field:Schema(description = "가격", example = "10000", required = true)
    val price: Int,

    @field:Min(value = 0, message = "재고는 0 이상이어야 합니다")
    @field:Schema(description = "재고", example = "100", required = true)
    val stock: Int,

    @field:Size(max = 500, message = "설명은 500자 이하여야 합니다")
    @field:Schema(description = "상품 설명", example = "10,000 포인트 상품", required = false)
    val description: String? = null
)
