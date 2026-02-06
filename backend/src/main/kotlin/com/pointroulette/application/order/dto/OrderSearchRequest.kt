package com.pointroulette.application.order.dto

import com.pointroulette.common.model.PaginationRequest
import com.pointroulette.domain.order.OrderStatus
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.Pageable

/**
 * 주문 검색 요청 파라미터
 */
@Schema(description = "주문 검색 요청 파라미터")
data class OrderSearchRequest(
    @field:Min(0, message = "페이지 번호는 0 이상이어야 합니다")
    @field:Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
    val page: Int = 0,

    @field:Min(1, message = "페이지 크기는 1 이상이어야 합니다")
    @field:Max(100, message = "페이지 크기는 100 이하여야 합니다")
    @field:Schema(description = "페이지 크기 (1-100)", example = "10", defaultValue = "10")
    val size: Int = 10,

    @field:Schema(
        description = "정렬 (필드명,방향 형식. 예: createdAt,desc)",
        example = "createdAt,desc",
        defaultValue = "createdAt,desc"
    )
    val sort: String = "createdAt,desc",

    @field:Schema(description = "사용자 ID (관리자 전용 필터)", example = "1", required = false)
    val userId: Long? = null,

    @field:Schema(description = "주문 상태 (관리자 전용 필터)", example = "COMPLETED", required = false)
    val status: OrderStatus? = null
) {
    /**
     * Spring Data Pageable 객체로 변환
     */
    fun toPageable(): Pageable {
        return PaginationRequest.toPageable(page, size, sort, "createdAt")
    }
}
