package com.pointroulette.presentation.admin.order.swagger

import com.pointroulette.application.order.dto.OrderResponse
import com.pointroulette.application.order.dto.OrderSearchRequest
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

@Tag(name = "Order Admin", description = "관리자 주문 관리 API")
interface AdminOrderControllerDocs {

    @Operation(
        summary = "주문 목록 조회 (관리자)",
        description = """
            전체 주문 목록을 페이징 처리하여 조회합니다.
            기본 정렬은 최신 주문 순(createdAt,desc)입니다.
            userId, status 파라미터로 필터링할 수 있습니다.
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
                                "productId": 1,
                                "productName": "포인트 10,000원",
                                "quantity": 1,
                                "totalPrice": 10000,
                                "status": "COMPLETED",
                                "createdAt": "2026-01-01T00:00:00"
                              }
                            ],
                            "page": 0,
                            "size": 10,
                            "totalElements": 1,
                            "totalPages": 1,
                            "first": true,
                            "last": true
                          }
                        }"""
                    )]
                )]
            )
        ]
    )
    fun getOrders(searchRequest: OrderSearchRequest): ResponseEntity<ResponseData<PaginationResponse<OrderResponse>>>

    @Operation(
        summary = "주문 취소 (포인트 환불)",
        description = """
            주문을 취소하고 포인트를 환불합니다.
            - 주문 상태가 CANCELLED로 변경됩니다.
            - 재고가 복구됩니다.
            - 사용된 포인트가 환불됩니다 (30일 유효기간).
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "주문 취소 성공",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = OrderResponse::class),
                    examples = [ExampleObject(
                        value = """{
                          "code": 200,
                          "message": "OK",
                          "data": {
                            "id": 1,
                            "userId": 1,
                            "productId": 1,
                            "productName": "포인트 10,000원",
                            "quantity": 1,
                            "totalPrice": 10000,
                            "status": "CANCELLED",
                            "createdAt": "2026-01-01T00:00:00"
                          }
                        }"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "주문을 찾을 수 없음",
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
                description = "이미 취소된 주문",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        value = """{
                          "code": 400,
                          "message": "BAD_REQUEST",
                          "errors": {
                            "message": "이미 취소된 주문입니다."
                          }
                        }"""
                    )]
                )]
            )
        ]
    )
    fun cancelOrder(orderId: Long): ResponseEntity<ResponseData<OrderResponse>>
}
