package com.pointroulette.presentation.point.swagger

import com.pointroulette.application.point.dto.PointBalanceResponse
import com.pointroulette.presentation.common.dto.ResponseData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Point", description = "포인트 조회 API")
interface PointControllerDocs {

    @Operation(
        summary = "내 포인트 조회",
        description = """
            사용자의 포인트 잔액 및 포인트 목록을 조회합니다.
            - 현재 포인트 잔액
            - 7일 이내 만료 예정 포인트
            - 보유한 포인트 목록 (만료일 오름차순, 유효기간 포함)
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(schema = Schema(implementation = PointBalanceResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ResponseData::class))]
            )
        ]
    )
    fun getPointBalance(
        @Parameter(description = "사용자 ID", required = true, example = "1")
        userId: Long
    ): ResponseEntity<ResponseData<PointBalanceResponse>>
}
