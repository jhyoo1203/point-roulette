package com.pointroulette.presentation.product

import com.pointroulette.application.order.OrderService
import com.pointroulette.application.order.dto.OrderCreateRequest
import com.pointroulette.application.order.dto.OrderResponse
import com.pointroulette.application.order.dto.OrderSearchRequest
import com.pointroulette.application.product.ProductService
import com.pointroulette.application.product.dto.ProductResponse
import com.pointroulette.application.product.dto.ProductSearchRequest
import com.pointroulette.common.model.PaginationResponse
import com.pointroulette.domain.product.ProductStatus
import com.pointroulette.presentation.common.dto.ResponseData
import com.pointroulette.presentation.product.swagger.ProductControllerDocs
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 상품 Controller (사용자용)
 */
@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService,
    private val orderService: OrderService
) : ProductControllerDocs {

    @GetMapping
    override fun getProducts(
        @ModelAttribute searchRequest: ProductSearchRequest
    ): ResponseEntity<ResponseData<PaginationResponse<ProductResponse>>> {
        // 사용자는 ACTIVE 상품만 조회 가능
        val activeSearchRequest = searchRequest.copy(status = ProductStatus.ACTIVE)
        val response = productService.getProducts(activeSearchRequest)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }

    @PostMapping("/purchase/{userId}")
    override fun purchaseProduct(
        @PathVariable userId: Long,
        @Valid @RequestBody request: OrderCreateRequest
    ): ResponseEntity<ResponseData<OrderResponse>> {
        val response = orderService.createOrder(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseData.of(HttpStatus.CREATED, response))
    }

    @GetMapping("/orders/{userId}")
    override fun getOrderHistory(
        @PathVariable userId: Long,
        @ModelAttribute searchRequest: OrderSearchRequest
    ): ResponseEntity<ResponseData<PaginationResponse<OrderResponse>>> {
        val response = orderService.getOrderHistory(userId, searchRequest)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }
}
