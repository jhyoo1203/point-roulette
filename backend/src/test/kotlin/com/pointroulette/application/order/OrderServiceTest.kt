package com.pointroulette.application.order

import com.pointroulette.application.order.dto.OrderCreateRequest
import com.pointroulette.application.point.PointService
import com.pointroulette.application.user.UserService
import com.pointroulette.domain.order.Order
import com.pointroulette.domain.order.OrderRepository
import com.pointroulette.domain.order.OrderStatus
import com.pointroulette.domain.product.Product
import com.pointroulette.domain.product.ProductRepository
import com.pointroulette.domain.product.ProductStatus
import com.pointroulette.domain.user.User
import com.pointroulette.helper.TestEntityFactory
import com.pointroulette.presentation.exception.ResourceNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

/**
 * OrderService 단위 테스트
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("OrderService 테스트")
class OrderServiceTest {

    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var pointService: PointService

    @InjectMocks
    private lateinit var orderService: OrderService

    @Nested
    @DisplayName("createOrder 메서드")
    inner class CreateOrderTest {

        @Test
        @DisplayName("유효한 요청이면 주문을 생성하고 포인트를 차감한다")
        fun `should create order and deduct points when request is valid`() {
            // Given
            val userId = 1L
            val productId = 1L
            val orderId = 1L
            val request = OrderCreateRequest(
                productId = productId,
                quantity = 2
            )

            val user = TestEntityFactory.createUser(
                id = userId,
                nickname = "testuser",
                currentPoint = 20000
            )
            val product = TestEntityFactory.createProduct(
                id = productId,
                name = "포인트 10,000원",
                price = 10000,
                stock = 100,
                status = ProductStatus.ACTIVE
            )

            val order = TestEntityFactory.createOrder(
                id = orderId,
                user = user,
                product = product,
                quantity = request.quantity,
                totalPrice = 20000,
                status = OrderStatus.COMPLETED
            )

            given(userService.getUser(userId)).willReturn(user)
            given(productRepository.findById(productId)).willReturn(Optional.of(product))
            given(orderRepository.save(any())).willReturn(order)

            // When
            val response = orderService.createOrder(userId, request)

            // Then
            assertThat(response.userId).isEqualTo(userId)
            assertThat(response.productId).isEqualTo(productId)
            assertThat(response.quantity).isEqualTo(2)
            assertThat(response.totalPrice).isEqualTo(20000)
            assertThat(response.status).isEqualTo(OrderStatus.COMPLETED)
            assertThat(product.stock).isEqualTo(98) // 재고 차감 확인
            verify(pointService).usePoints(userId, 20000, orderId)
        }

        @Test
        @DisplayName("상품을 찾을 수 없으면 ResourceNotFoundException을 발생시킨다")
        fun `should throw ResourceNotFoundException when product not found`() {
            // Given
            val userId = 1L
            val request = OrderCreateRequest(
                productId = 999L,
                quantity = 1
            )

            val user = TestEntityFactory.createUser(
                id = userId,
                nickname = "testuser",
                currentPoint = 10000
            )
            given(userService.getUser(userId)).willReturn(user)
            given(productRepository.findById(999L)).willReturn(Optional.empty())

            // When & Then
            assertThatThrownBy { orderService.createOrder(userId, request) }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessageContaining("상품을 찾을 수 없습니다")
        }

        @Test
        @DisplayName("비활성 상품이면 IllegalStateException을 발생시킨다")
        fun `should throw IllegalStateException when product is inactive`() {
            // Given
            val userId = 1L
            val productId = 1L
            val request = OrderCreateRequest(
                productId = productId,
                quantity = 1
            )

            val user = TestEntityFactory.createUser(
                id = userId,
                nickname = "testuser",
                currentPoint = 10000
            )
            val product = TestEntityFactory.createProduct(
                id = productId,
                name = "비활성 상품",
                price = 10000,
                stock = 100,
                status = ProductStatus.INACTIVE
            )

            given(userService.getUser(userId)).willReturn(user)
            given(productRepository.findById(productId)).willReturn(Optional.of(product))

            // When & Then
            assertThatThrownBy { orderService.createOrder(userId, request) }
                .isInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("구매할 수 없는 상품입니다")
        }

        @Test
        @DisplayName("재고가 부족하면 IllegalStateException을 발생시킨다")
        fun `should throw IllegalStateException when stock is insufficient`() {
            // Given
            val userId = 1L
            val productId = 1L
            val request = OrderCreateRequest(
                productId = productId,
                quantity = 10
            )

            val user = TestEntityFactory.createUser(
                id = userId,
                nickname = "testuser",
                currentPoint = 100000
            )
            val product = TestEntityFactory.createProduct(
                id = productId,
                name = "재고부족 상품",
                price = 10000,
                stock = 5,
                status = ProductStatus.ACTIVE
            )

            given(userService.getUser(userId)).willReturn(user)
            given(productRepository.findById(productId)).willReturn(Optional.of(product))

            // When & Then
            assertThatThrownBy { orderService.createOrder(userId, request) }
                .isInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("재고가 부족합니다")
        }
    }

    @Nested
    @DisplayName("cancelOrder 메서드")
    inner class CancelOrderTest {

        @Test
        @DisplayName("유효한 주문을 취소하고 포인트를 환불한다")
        fun `should cancel order and refund points when order is valid`() {
            // Given
            val userId = 1L
            val productId = 1L
            val orderId = 1L

            val user = TestEntityFactory.createUser(
                id = userId,
                nickname = "testuser",
                currentPoint = 0
            )
            val product = TestEntityFactory.createProduct(
                id = productId,
                name = "포인트 10,000원",
                price = 10000,
                stock = 98,
                status = ProductStatus.ACTIVE
            )

            val order = TestEntityFactory.createOrder(
                id = orderId,
                user = user,
                product = product,
                quantity = 2,
                totalPrice = 20000,
                status = OrderStatus.COMPLETED
            )

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order))

            // When
            val response = orderService.cancelOrder(orderId)

            // Then
            assertThat(response.status).isEqualTo(OrderStatus.CANCELLED)
            assertThat(product.stock).isEqualTo(100) // 재고 복구 확인
            verify(pointService).refundPoints(userId, 20000, orderId)
        }

        @Test
        @DisplayName("주문을 찾을 수 없으면 ResourceNotFoundException을 발생시킨다")
        fun `should throw ResourceNotFoundException when order not found`() {
            // Given
            val orderId = 999L
            given(orderRepository.findById(orderId)).willReturn(Optional.empty())

            // When & Then
            assertThatThrownBy { orderService.cancelOrder(orderId) }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessageContaining("주문을 찾을 수 없습니다")
        }

        @Test
        @DisplayName("이미 취소된 주문이면 IllegalStateException을 발생시킨다")
        fun `should throw IllegalStateException when order is already cancelled`() {
            // Given
            val userId = 1L
            val productId = 1L
            val orderId = 1L

            val user = TestEntityFactory.createUser(
                id = userId,
                nickname = "testuser",
                currentPoint = 0
            )
            val product = TestEntityFactory.createProduct(
                id = productId,
                name = "포인트 10,000원",
                price = 10000,
                stock = 100,
                status = ProductStatus.ACTIVE
            )

            val order = TestEntityFactory.createOrder(
                id = orderId,
                user = user,
                product = product,
                quantity = 1,
                totalPrice = 10000,
                status = OrderStatus.CANCELLED
            )

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order))

            // When & Then
            assertThatThrownBy { orderService.cancelOrder(orderId) }
                .isInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("이미 취소된 주문입니다")
        }
    }
}
