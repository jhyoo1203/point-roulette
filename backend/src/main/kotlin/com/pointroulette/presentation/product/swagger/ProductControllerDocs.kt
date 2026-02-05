package com.pointroulette.presentation.product.swagger

import com.pointroulette.application.order.dto.OrderCreateRequest
import com.pointroulette.application.order.dto.OrderResponse
import com.pointroulette.application.product.dto.ProductResponse
import com.pointroulette.application.product.dto.ProductSearchRequest
import com.pointroulette.common.model.PaginationResponse
import com.pointroulette.presentation.common.dto.ResponseData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Product", description = "상품 API (사용자용)")
interface ProductControllerDocs {

    @Operation(
        summary = "상품 목록 조회",
        description = """
            상품 목록을 페이징하여 조회합니다.
            - 활성 상태인 상품만 조회됩니다.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "상품 목록 조회 성공",
                content = [Content(schema = Schema(implementation = ResponseData::class))]
            )
        ]
    )
    fun getProducts(
        @Parameter(description = "검색 조건 (page, size, sort)")
        searchRequest: ProductSearchRequest
    ): ResponseEntity<ResponseData<PaginationResponse<ProductResponse>>>

    @Operation(
        summary = "상품 구매",
        description = """
            상품을 구매하고 주문을 생성합니다.
            - 포인트가 FIFO 방식으로 차감됩니다.
            - 재고가 차감됩니다.
            - 주문 내역이 생성됩니다.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "상품 구매 성공",
                content = [Content(schema = Schema(implementation = OrderResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (수량 부족, 비활성 상품 등)",
                content = [Content(schema = Schema(implementation = ResponseData::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "상품을 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ResponseData::class))]
            ),
            ApiResponse(
                responseCode = "422",
                description = "포인트 부족",
                content = [Content(schema = Schema(implementation = ResponseData::class))]
            )
        ]
    )
    fun purchaseProduct(
        @Parameter(description = "사용자 ID", required = true, example = "1")
        userId: Long,
        @RequestBody(
            description = "주문 생성 요청",
            required = true
        )
        request: OrderCreateRequest
    ): ResponseEntity<ResponseData<OrderResponse>>
}
