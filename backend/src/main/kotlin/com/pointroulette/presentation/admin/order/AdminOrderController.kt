package com.pointroulette.presentation.admin.order

import com.pointroulette.application.order.OrderService
import com.pointroulette.application.order.dto.OrderResponse
import com.pointroulette.application.order.dto.OrderSearchRequest
import com.pointroulette.common.model.PaginationResponse
import com.pointroulette.presentation.admin.order.swagger.AdminOrderControllerDocs
import com.pointroulette.presentation.common.dto.ResponseData
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/orders")
class AdminOrderController(
    private val orderService: OrderService
) : AdminOrderControllerDocs {

    @GetMapping
    override fun getOrders(
        @ParameterObject @Valid @ModelAttribute searchRequest: OrderSearchRequest
    ): ResponseEntity<ResponseData<PaginationResponse<OrderResponse>>> {
        val response = orderService.getAllOrders(searchRequest)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }

    @PostMapping("/{orderId}/cancel")
    override fun cancelOrder(
        @PathVariable orderId: Long
    ): ResponseEntity<ResponseData<OrderResponse>> {
        val response = orderService.cancelOrder(orderId)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }
}
