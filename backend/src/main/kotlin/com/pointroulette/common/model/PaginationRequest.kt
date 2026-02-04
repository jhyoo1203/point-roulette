package com.pointroulette.common.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * 페이징 요청 파라미터
 */
@Schema(description = "페이징 요청 파라미터")
data class PaginationRequest(
    @field:Min(0, message = "페이지 번호는 0 이상이어야 합니다")
    @field:Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
    val page: Int = 0,

    @field:Min(1, message = "페이지 크기는 1 이상이어야 합니다")
    @field:Max(100, message = "페이지 크기는 100 이하여야 합니다")
    @field:Schema(description = "페이지 크기 (1-100)", example = "10", defaultValue = "10")
    val size: Int = 10,

    @field:Schema(
        description = "정렬 (필드명,방향 형식. 예: updatedAt,desc, createdAt,asc)",
        example = "updatedAt,desc",
        defaultValue = "updatedAt,desc"
    )
    val sort: String = "updatedAt,desc"
) {

    companion object {
        /**
         * 페이징 파라미터를 Spring Data Pageable 객체로 변환
         * @param page 페이지 번호
         * @param size 페이지 크기
         * @param sort 정렬 문자열 (필드명,방향 형식)
         * @param defaultProperty 기본 정렬 필드명
         */
        fun toPageable(
            page: Int,
            size: Int,
            sort: String,
            defaultProperty: String
        ): Pageable {
            val sortParts = sort.split(",")
            val property = sortParts.getOrNull(0) ?: defaultProperty
            val direction = when (sortParts.getOrNull(1)?.uppercase()) {
                "ASC" -> Sort.Direction.ASC
                "DESC" -> Sort.Direction.DESC
                else -> Sort.Direction.DESC
            }

            return PageRequest.of(page, size, Sort.by(direction, property))
        }
    }
}
