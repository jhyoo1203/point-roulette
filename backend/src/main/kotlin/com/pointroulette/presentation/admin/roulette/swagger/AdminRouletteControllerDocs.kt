package com.pointroulette.presentation.admin.roulette.swagger

import com.pointroulette.application.roulette.dto.RouletteHistoryResponse
import com.pointroulette.application.roulette.dto.RouletteParticipationResponse
import com.pointroulette.application.roulette.dto.RouletteParticipationSearchRequest
import com.pointroulette.common.model.PaginationResponse
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
        summary = "룰렛 참여 이력 조회",
        description = """
            날짜 범위로 룰렛 참여 이력을 페이징 처리하여 조회합니다.
            - startDate, endDate 파라미터로 조회 범위를 지정합니다.
            - 기본 정렬은 참여 날짜 역순(participatedDate,desc)입니다.
            - page, size, sort 파라미터로 페이징과 정렬을 제어할 수 있습니다.
            - N+1 문제를 방지하기 위해 fetch join을 사용합니다.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        value = """{
                          "code": 200,
                          "message": "OK",
                          "data": {
                            "content": [
                              {
                                "id": 1,
                                "userId": 1,
                                "userName": "홍길동",
                                "participatedDate": "2026-02-06",
                                "wonAmount": 500,
                                "status": "SUCCESS"
                              },
                              {
                                "id": 2,
                                "userId": 2,
                                "userName": "김철수",
                                "participatedDate": "2026-02-06",
                                "wonAmount": 800,
                                "status": "SUCCESS"
                              }
                            ],
                            "totalElements": 2,
                            "totalPages": 1,
                            "currentPage": 0,
                            "pageSize": 10,
                            "hasNext": false,
                            "hasPrevious": false
                          }
                        }"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (Validation 실패)",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        value = """{
                          "code": 400,
                          "message": "BAD_REQUEST",
                          "errors": {
                            "startDate": "시작일은 필수입니다",
                            "endDate": "종료일은 필수입니다"
                          }
                        }"""
                    )]
                )]
            )
        ]
    )
    fun getParticipations(searchRequest: RouletteParticipationSearchRequest): ResponseEntity<ResponseData<PaginationResponse<RouletteParticipationResponse>>>


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
