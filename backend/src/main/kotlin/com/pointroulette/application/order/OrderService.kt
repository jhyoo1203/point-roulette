package com.pointroulette.application.order

import com.pointroulette.application.order.dto.OrderCreateRequest
import com.pointroulette.application.order.dto.OrderResponse
import com.pointroulette.application.order.dto.OrderSearchRequest
import com.pointroulette.application.point.PointService
import com.pointroulette.application.user.UserService
import com.pointroulette.common.model.PaginationResponse
import com.pointroulette.domain.order.Order
import com.pointroulette.domain.order.OrderRepository
import com.pointroulette.domain.order.OrderStatus
import com.pointroulette.domain.product.ProductRepository
import com.pointroulette.domain.product.ProductStatus
import com.pointroulette.presentation.exception.ResourceNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 주문 서비스
 */
@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val userService: UserService,
    private val pointService: PointService
) {

    /**
     * 주문을 생성합니다.
     * - 재고를 차감합니다.
     * - 포인트를 FIFO 방식으로 차감합니다.
     * - 주문을 저장합니다.
     *
     * @param userId 사용자 ID
     * @param request 주문 생성 요청
     * @return 생성된 주문 응답
     * @throws ResourceNotFoundException 상품을 찾을 수 없는 경우
     * @throws IllegalStateException 상품이 비활성 상태이거나 재고가 부족한 경우
     */
    @Transactional
    fun createOrder(userId: Long, request: OrderCreateRequest): OrderResponse {
        val user = userService.getUser(userId)

        val product = productRepository.findByIdOrNull(request.productId)
            ?: throw ResourceNotFoundException("상품을 찾을 수 없습니다. (ID: ${request.productId})")

        // 상품 상태 확인
        if (product.status != ProductStatus.ACTIVE) {
            throw IllegalStateException("구매할 수 없는 상품입니다. (상태: ${product.status})")
        }

        // 재고 확인 및 차감
        if (product.stock < request.quantity) {
            throw IllegalStateException("재고가 부족합니다. (요청: ${request.quantity}, 재고: ${product.stock})")
        }
        product.stock -= request.quantity

        // 총 가격 계산
        val totalPrice = product.price * request.quantity

        // 주문 생성
        val order = Order(
            user = user,
            product = product,
            quantity = request.quantity,
            totalPrice = totalPrice,
            status = OrderStatus.COMPLETED
        )
        val savedOrder = orderRepository.save(order)

        // 포인트 차감
        pointService.usePoints(
            userId = userId,
            amount = totalPrice,
            orderId = savedOrder.id
        )

        return OrderResponse.from(savedOrder)
    }

    /**
     * 주문을 취소합니다. (관리자 전용)
     * - 주문 상태를 CANCELLED로 변경합니다.
     * - 재고를 복구합니다.
     * - 포인트를 환불합니다.
     *
     * @param orderId 주문 ID
     * @return 취소된 주문 응답
     * @throws ResourceNotFoundException 주문을 찾을 수 없는 경우
     * @throws IllegalStateException 이미 취소된 주문인 경우
     */
    @Transactional
    fun cancelOrder(orderId: Long): OrderResponse {
        val order = orderRepository.findByIdOrNull(orderId)
            ?: throw ResourceNotFoundException("주문을 찾을 수 없습니다. (ID: $orderId)")

        // 이미 취소된 주문인지 확인
        if (order.status == OrderStatus.CANCELLED) {
            throw IllegalStateException("이미 취소된 주문입니다.")
        }

        // 주문 상태 변경
        order.status = OrderStatus.CANCELLED

        // 재고 복구
        order.product.stock += order.quantity

        // 포인트 환불
        pointService.refundPoints(
            userId = order.user.id,
            amount = order.totalPrice,
            orderId = orderId
        )

        return OrderResponse.from(order)
    }

    /**
     * 사용자의 주문 내역을 페이징하여 조회합니다.
     * - 최신순으로 정렬
     *
     * @param userId 사용자 ID
     * @param searchRequest 검색 조건 (페이징, 정렬)
     * @return 주문 내역 페이징 응답
     */
    @Transactional(readOnly = true)
    fun getOrderHistory(userId: Long, searchRequest: OrderSearchRequest): PaginationResponse<OrderResponse> {
        val pageable = searchRequest.toPageable()
        val user = userService.getUser(userId)
        val page = orderRepository.findAllByUserId(user.id, pageable)
        return PaginationResponse.from(page, OrderResponse::from)
    }

    /**
     * 관리자용 전체 주문 목록을 조회합니다. (필터링 지원)
     * - userId, status로 필터링 가능
     * - 최신순으로 정렬
     *
     * @param searchRequest 검색 조건 (페이징, 정렬, 필터)
     * @return 주문 목록 페이징 응답
     */
    @Transactional(readOnly = true)
    fun getAllOrders(searchRequest: OrderSearchRequest): PaginationResponse<OrderResponse> {
        val pageable = searchRequest.toPageable()
        val page = orderRepository.findAllWithFilters(
            userId = searchRequest.userId,
            status = searchRequest.status,
            pageable = pageable
        )
        return PaginationResponse.from(page, OrderResponse::from)
    }
}
