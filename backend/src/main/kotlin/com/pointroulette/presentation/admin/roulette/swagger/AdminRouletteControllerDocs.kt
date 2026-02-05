package com.pointroulette.presentation.admin.roulette.swagger

import com.pointroulette.application.roulette.dto.RouletteHistoryResponse
import com.pointroulette.presentation.common.dto.ResponseData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Roulette Admin", description = "관리자 룰렛 관리 API")
interface AdminRouletteControllerDocs {

    @Operation(
        summary = "룰렛 참여 취소 (포인트 회수)",
        description = """
            룰렛 참여를 취소하고 포인트를 회수합니다.
            - 룰렛 참여 이력 상태가 CANCELLED로 변경됩니다.
            - 예산이 복구됩니다.
            - 획득한 포인트가 회수됩니다 (사용자 잔액에서 차감).
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "룰렛 참여 취소 성공",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RouletteHistoryResponse::class),
                    examples = [ExampleObject(
                        value = """{
                          "code": 200,
                          "message": "OK",
                          "data": {
                            "id": 1,
                            "participatedDate": "2026-02-06",
                            "wonAmount": 500,
                            "status": "CANCELLED"
                          }
                        }"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "룰렛 참여 이력을 찾을 수 없음",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        value = """{
                          "code": 404,
                          "message": "RESOURCE_NOT_FOUND"
                        }"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "이미 취소된 참여",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        value = """{
                          "code": 400,
                          "message": "BAD_REQUEST",
                          "errors": {
                            "message": "이미 취소된 룰렛 참여입니다."
                          }
                        }"""
                    )]
                )]
            )
        ]
    )
    fun cancelParticipation(historyId: Long): ResponseEntity<ResponseData<RouletteHistoryResponse>>
}
