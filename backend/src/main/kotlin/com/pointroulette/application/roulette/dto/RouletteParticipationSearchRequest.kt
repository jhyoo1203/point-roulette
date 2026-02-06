package com.pointroulette.application.roulette.dto

import com.pointroulette.common.model.PaginationRequest
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.data.domain.Pageable
import java.time.LocalDate

/**
 * 룰렛 참여 이력 검색 요청 파라미터
 */
@Schema(description = "룰렛 참여 이력 검색 요청")
data class RouletteParticipationSearchRequest(
    @field:NotNull(message = "시작일은 필수입니다")
    @field:Schema(description = "시작일", example = "2026-02-06")
    val startDate: LocalDate,

    @field:NotNull(message = "종료일은 필수입니다")
    @field:Schema(description = "종료일", example = "2026-02-06")
    val endDate: LocalDate,

    @field:Min(0, message = "페이지 번호는 0 이상이어야 합니다")
    @field:Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
    val page: Int = 0,

    @field:Min(1, message = "페이지 크기는 1 이상이어야 합니다")
    @field:Max(100, message = "페이지 크기는 100 이하여야 합니다")
    @field:Schema(description = "페이지 크기 (1-100)", example = "10", defaultValue = "10")
    val size: Int = 10,

    @field:Schema(
        description = "정렬 (필드명,방향 형식. 예: participatedDate,desc, participatedDate,asc)",
        example = "participatedDate,desc",
        defaultValue = "participatedDate,desc"
    )
    val sort: String = "participatedDate,desc"
) {
    /**
     * Spring Data Pageable 객체로 변환
     */
    fun toPageable(): Pageable {
        return PaginationRequest.toPageable(page, size, sort, "participatedDate")
    }
}
