package com.pointroulette.presentation.roulette.swagger

import com.pointroulette.application.roulette.dto.RouletteParticipateResponse
import com.pointroulette.application.roulette.dto.RouletteStatusResponse
import com.pointroulette.presentation.common.dto.ResponseData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Roulette", description = "룰렛 참여 API")
interface RouletteControllerDocs {

    @Operation(
        summary = "룰렛 참여",
        description = """
            오늘의 룰렛에 참여합니다.
            - 1일 1회만 참여 가능합니다.
            - 100-1000 포인트를 랜덤하게 획득할 수 있습니다.
            - 일일 예산이 소진되면 꽝 처리됩니다.
            - 낙관적 락을 사용하여 동시성을 제어합니다.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "룰렛 참여 성공",
                content = [Content(schema = Schema(implementation = RouletteParticipateResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ResponseData::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "중복 참여 (오늘 이미 참여함)",
                content = [Content(schema = Schema(implementation = ResponseData::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "룰렛 참여 실패 (재시도 필요)",
                content = [Content(schema = Schema(implementation = ResponseData::class))]
            )
        ]
    )
    fun participate(
        @Parameter(description = "사용자 ID", required = true, example = "1")
        userId: Long
    ): ResponseEntity<ResponseData<RouletteParticipateResponse>>

    @Operation(
        summary = "룰렛 상태 조회",
        description = """
            오늘의 룰렛 참여 상태를 조회합니다.
            - 오늘 참여 여부
            - 오늘 참여 가능 여부 (예산 소진 체크)
            - 오늘 남은 예산
            - 마지막 참여 이력
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(schema = Schema(implementation = RouletteStatusResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ResponseData::class))]
            )
        ]
    )
    fun getStatus(
        @Parameter(description = "사용자 ID", required = true, example = "1")
        userId: Long
    ): ResponseEntity<ResponseData<RouletteStatusResponse>>
}