package com.pointroulette.presentation.admin.budget.swagger

import com.pointroulette.application.budget.dto.DailyBudgetCreateRequest
import com.pointroulette.application.budget.dto.DailyBudgetResponse
import com.pointroulette.application.budget.dto.DailyBudgetSearchRequest
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

@Tag(name = "Budget Admin", description = "관리자 일일 예산 관리 API")
interface AdminBudgetControllerDocs {

    @Operation(
        summary = "일일 예산 생성",
        description = """
            날짜 범위로 일일 예산을 생성합니다.
            - 각 날짜별로 100,000p의 예산이 생성됩니다.
            - 이미 존재하는 날짜는 건너뜁니다.
            - JPA Batch Insert를 사용하여 대량 데이터도 효율적으로 처리합니다.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "예산 생성 성공",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = DailyBudgetResponse::class),
                    examples = [ExampleObject(
                        value = """{
                          "code": 201,
                          "message": "CREATED",
                          "data": [
                            {
                              "id": 1,
                              "budgetDate": "2026-01-01",
                              "totalAmount": 100000,
                              "remainingAmount": 100000,
                              "createdAt": "2026-01-01T00:00:00",
                              "updatedAt": "2026-01-01T00:00:00"
                            },
                            {
                              "id": 2,
                              "budgetDate": "2026-01-02",
                              "totalAmount": 100000,
                              "remainingAmount": 100000,
                              "createdAt": "2026-01-01T00:00:00",
                              "updatedAt": "2026-01-01T00:00:00"
                            }
                          ]
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
    fun createDailyBudgets(request: DailyBudgetCreateRequest): ResponseEntity<ResponseData<List<DailyBudgetResponse>>>

    @Operation(
        summary = "일일 예산 조회",
        description = """
            날짜 범위로 일일 예산을 페이징 처리하여 조회합니다.
            - startDate, endDate 파라미터로 조회 범위를 지정합니다.
            - 기본 정렬은 예산 날짜 역순(budgetDate,desc)입니다.
            - page, size, sort 파라미터로 페이징과 정렬을 제어할 수 있습니다.
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
                                "budgetDate": "2026-01-01",
                                "totalAmount": 100000,
                                "remainingAmount": 50000,
                                "createdAt": "2026-01-01T00:00:00",
                                "updatedAt": "2026-01-01T12:00:00"
                              },
                              {
                                "id": 2,
                                "budgetDate": "2026-01-02",
                                "totalAmount": 100000,
                                "remainingAmount": 100000,
                                "createdAt": "2026-01-01T00:00:00",
                                "updatedAt": "2026-01-01T00:00:00"
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
    fun getDailyBudgets(searchRequest: DailyBudgetSearchRequest): ResponseEntity<ResponseData<PaginationResponse<DailyBudgetResponse>>>
}
