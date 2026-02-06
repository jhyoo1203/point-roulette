package com.pointroulette.presentation.admin.product.swagger

import com.pointroulette.application.product.dto.ProductCreateRequest
import com.pointroulette.application.product.dto.ProductResponse
import com.pointroulette.application.product.dto.ProductSearchRequest
import com.pointroulette.application.product.dto.ProductUpdateRequest
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

@Tag(name = "Product Admin", description = "관리자 상품 관리 API")
interface AdminProductControllerDocs {

    @Operation(
        summary = "상품 생성",
        description = "새로운 상품을 생성합니다. 생성된 상품의 초기 상태는 ACTIVE입니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "상품 생성 성공",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ProductResponse::class),
                    examples = [ExampleObject(
                        value = """{
                          "code": 201,
                          "message": "CREATED",
                          "data": {
                            "id": 1,
                            "name": "포인트 10,000원",
                            "price": 10000,
                            "stock": 100,
                            "description": "10,000 포인트 상품",
                            "status": "ACTIVE",
                            "createdAt": "2026-01-01T00:00:00",
                            "updatedAt": "2026-01-01T00:00:00"
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
                            "name": "상품명은 필수입니다",
                            "price": "가격은 0 이상이어야 합니다"
                          }
                        }"""
                    )]
                )]
            )
        ]
    )
    fun createProduct(request: ProductCreateRequest): ResponseEntity<ResponseData<ProductResponse>>

    @Operation(
        summary = "상품 단건 조회",
        description = "ID로 특정 상품을 조회합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ProductResponse::class),
                    examples = [ExampleObject(
                        value = """{
                          "code": 200,
                          "message": "OK",
                          "data": {
                            "id": 1,
                            "name": "포인트 10,000원",
                            "price": 10000,
                            "stock": 100,
                            "description": "10,000 포인트 상품",
                            "status": "ACTIVE",
                            "createdAt": "2026-01-01T00:00:00",
                            "updatedAt": "2026-01-01T00:00:00"
                          }
                        }"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "상품을 찾을 수 없음",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        value = """{
                          "code": 404,
                          "message": "RESOURCE_NOT_FOUND"
                        }"""
                    )]
                )]
            )
        ]
    )
    fun getProduct(id: Long): ResponseEntity<ResponseData<ProductResponse>>

    @Operation(
        summary = "상품 목록 조회",
        description = """
            상품 목록을 페이징 처리하여 조회합니다.
            기본 정렬은 최근 수정 순(updatedAt,desc)입니다.
            status 파라미터로 상품 상태를 필터링할 수 있습니다.
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
                                "name": "포인트 10,000원",
                                "price": 10000,
                                "stock": 100,
                                "description": "10,000 포인트 상품",
                                "status": "ACTIVE",
                                "createdAt": "2026-01-01T00:00:00",
                                "updatedAt": "2026-01-01T00:00:00"
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
    fun getProducts(searchRequest: ProductSearchRequest): ResponseEntity<ResponseData<PaginationResponse<ProductResponse>>>

    @Operation(
        summary = "상품 수정",
        description = "상품 정보를 수정합니다. Partial Update를 지원하여 제공된 필드만 업데이트됩니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "수정 성공",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ProductResponse::class),
                    examples = [ExampleObject(
                        value = """{
                          "code": 200,
                          "message": "OK",
                          "data": {
                            "id": 1,
                            "name": "포인트 15,000원",
                            "price": 15000,
                            "stock": 100,
                            "description": "10,000 포인트 상품",
                            "status": "ACTIVE",
                            "createdAt": "2026-01-01T00:00:00",
                            "updatedAt": "2026-01-01T01:00:00"
                          }
                        }"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "상품을 찾을 수 없음",
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
                description = "잘못된 요청 (Validation 실패)",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        value = """{
                          "code": 400,
                          "message": "BAD_REQUEST",
                          "errors": {
                            "price": "가격은 0 이상이어야 합니다"
                          }
                        }"""
                    )]
                )]
            )
        ]
    )
    fun updateProduct(id: Long, request: ProductUpdateRequest): ResponseEntity<ResponseData<ProductResponse>>

    @Operation(
        summary = "상품 삭제 (Soft Delete)",
        description = "상품을 삭제합니다. 물리적 삭제가 아닌 상태를 INACTIVE로 변경하는 Soft Delete 방식입니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "삭제 성공",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        value = """{
                          "code": 200,
                          "message": "OK"
                        }"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "상품을 찾을 수 없음",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        value = """{
                          "code": 404,
                          "message": "RESOURCE_NOT_FOUND"
                        }"""
                    )]
                )]
            )
        ]
    )
    fun deleteProduct(id: Long): ResponseEntity<ResponseData<Unit>>
}
