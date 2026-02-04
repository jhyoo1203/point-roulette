package com.pointroulette.application.budget.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.time.LocalDate

/**
 * 일일 예산 검색 요청 파라미터
 */
@Schema(description = "일일 예산 검색 요청")
data class DailyBudgetSearchRequest(
    @field:NotNull(message = "시작일은 필수입니다")
    @field:Schema(description = "시작일", example = "2026-01-01")
    val startDate: LocalDate,

    @field:NotNull(message = "종료일은 필수입니다")
    @field:Schema(description = "종료일", example = "2026-12-31")
    val endDate: LocalDate,

    @field:Min(0, message = "페이지 번호는 0 이상이어야 합니다")
    @field:Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
    val page: Int = 0,

    @field:Min(1, message = "페이지 크기는 1 이상이어야 합니다")
    @field:Max(100, message = "페이지 크기는 100 이하여야 합니다")
    @field:Schema(description = "페이지 크기 (1-100)", example = "10", defaultValue = "10")
    val size: Int = 10,

    @field:Schema(
        description = "정렬 (필드명,방향 형식. 예: budgetDate,desc, budgetDate,asc)",
        example = "budgetDate,desc",
        defaultValue = "budgetDate,desc"
    )
    val sort: String = "budgetDate,desc"
) {
    /**
     * Spring Data Pageable 객체로 변환
     * sort 문자열을 파싱하여 Sort.Direction과 필드명으로 분리
     */
    fun toPageable(): Pageable {
        val sortParts = sort.split(",")
        val property = sortParts.getOrNull(0) ?: "budgetDate"
        val direction = when (sortParts.getOrNull(1)?.uppercase()) {
            "ASC" -> Sort.Direction.ASC
            "DESC" -> Sort.Direction.DESC
            else -> Sort.Direction.DESC
        }

        return PageRequest.of(page, size, Sort.by(direction, property))
    }
}
