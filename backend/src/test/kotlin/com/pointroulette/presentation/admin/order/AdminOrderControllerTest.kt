package com.pointroulette.presentation.admin.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.pointroulette.domain.order.Order
import com.pointroulette.domain.order.OrderRepository
import com.pointroulette.domain.order.OrderStatus
import com.pointroulette.domain.product.Product
import com.pointroulette.domain.product.ProductRepository
import com.pointroulette.domain.product.ProductStatus
import com.pointroulette.domain.user.User
import com.pointroulette.domain.user.UserRepository
import com.pointroulette.helper.DatabaseCleaner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * AdminOrderController 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DisplayName("AdminOrderController 통합 테스트")
class AdminOrderControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var databaseCleaner: DatabaseCleaner

    @BeforeEach
    fun setUp() {
        databaseCleaner.clear()
    }

    @Nested
    @DisplayName("GET /api/v1/admin/orders")
    inner class GetOrdersApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("전체 주문 목록을 페이징하여 조회할 수 있다")
            fun `should return all orders with pagination`() {
                // Given
                val user = userRepository.save(User(nickname = "testuser", currentPoint = 0))
                val product = productRepository.save(
                    Product(name = "포인트 10,000원", price = 10000, stock = 100, status = ProductStatus.ACTIVE)
                )
                orderRepository.save(
                    Order(user = user, product = product, quantity = 1, totalPrice = 10000, status = OrderStatus.COMPLETED)
                )
                orderRepository.save(
                    Order(user = user, product = product, quantity = 2, totalPrice = 20000, status = OrderStatus.COMPLETED)
                )

                // When & Then
                mockMvc.get("/api/v1/admin/orders") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(2) }
                    jsonPath("$.data.totalElements") { value(2) }
                    jsonPath("$.data.currentPage") { value(0) }
                    jsonPath("$.data.pageSize") { value(10) }
                }
            }

            @Test
            @DisplayName("userId 필터로 특정 사용자의 주문만 조회할 수 있다")
            fun `should return orders filtered by userId`() {
                // Given
                val user1 = userRepository.save(User(nickname = "user1", currentPoint = 0))
                val user2 = userRepository.save(User(nickname = "user2", currentPoint = 0))
                val product = productRepository.save(
                    Product(name = "포인트 10,000원", price = 10000, stock = 100, status = ProductStatus.ACTIVE)
                )
                orderRepository.save(
                    Order(user = user1, product = product, quantity = 1, totalPrice = 10000, status = OrderStatus.COMPLETED)
                )
                orderRepository.save(
                    Order(user = user2, product = product, quantity = 2, totalPrice = 20000, status = OrderStatus.COMPLETED)
                )

                // When & Then
                mockMvc.get("/api/v1/admin/orders?userId=${user1.id}") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(1) }
                    jsonPath("$.data.content[0].userId") { value(user1.id) }
                }
            }

            @Test
            @DisplayName("status 필터로 특정 상태의 주문만 조회할 수 있다")
            fun `should return orders filtered by status`() {
                // Given
                val user = userRepository.save(User(nickname = "testuser", currentPoint = 0))
                val product = productRepository.save(
                    Product(name = "포인트 10,000원", price = 10000, stock = 100, status = ProductStatus.ACTIVE)
                )
                orderRepository.save(
                    Order(user = user, product = product, quantity = 1, totalPrice = 10000, status = OrderStatus.COMPLETED)
                )
                orderRepository.save(
                    Order(user = user, product = product, quantity = 2, totalPrice = 20000, status = OrderStatus.CANCELLED)
                )

                // When & Then
                mockMvc.get("/api/v1/admin/orders?status=COMPLETED") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(1) }
                    jsonPath("$.data.content[0].status") { value("COMPLETED") }
                }
            }

            @Test
            @DisplayName("userId와 status를 동시에 필터링할 수 있다")
            fun `should return orders filtered by both userId and status`() {
                // Given
                val user1 = userRepository.save(User(nickname = "user1", currentPoint = 0))
                val user2 = userRepository.save(User(nickname = "user2", currentPoint = 0))
                val product = productRepository.save(
                    Product(name = "포인트 10,000원", price = 10000, stock = 100, status = ProductStatus.ACTIVE)
                )
                orderRepository.save(
                    Order(user = user1, product = product, quantity = 1, totalPrice = 10000, status = OrderStatus.COMPLETED)
                )
                orderRepository.save(
                    Order(user = user1, product = product, quantity = 2, totalPrice = 20000, status = OrderStatus.CANCELLED)
                )
                orderRepository.save(
                    Order(user = user2, product = product, quantity = 1, totalPrice = 10000, status = OrderStatus.COMPLETED)
                )

                // When & Then
                mockMvc.get("/api/v1/admin/orders?userId=${user1.id}&status=COMPLETED") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(1) }
                    jsonPath("$.data.content[0].userId") { value(user1.id) }
                    jsonPath("$.data.content[0].status") { value("COMPLETED") }
                }
            }

            @Test
            @DisplayName("주문이 없어도 빈 페이징 응답을 반환한다")
            fun `should return empty pagination response when no orders`() {
                // When & Then
                mockMvc.get("/api/v1/admin/orders") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(0) }
                    jsonPath("$.data.totalElements") { value(0) }
                }
            }
        }
    }

    @Nested
    @DisplayName("POST /api/v1/admin/orders/{orderId}/cancel")
    inner class CancelOrderApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("유효한 주문을 취소하면 200 응답과 취소된 주문 정보를 반환한다")
            fun `should return 200 with cancelled order when order is valid`() {
                // Given
                val user = userRepository.save(User(nickname = "testuser", currentPoint = 0))
                val product = productRepository.save(
                    Product(name = "포인트 10,000원", price = 10000, stock = 98, status = ProductStatus.ACTIVE)
                )
                val order = orderRepository.save(
                    Order(
                        user = user,
                        product = product,
                        quantity = 2,
                        totalPrice = 20000,
                        status = OrderStatus.COMPLETED
                    )
                )

                // When & Then
                mockMvc.post("/api/v1/admin/orders/${order.id}/cancel") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.id") { value(order.id) }
                    jsonPath("$.data.status") { value("CANCELLED") }
                    jsonPath("$.data.totalPrice") { value(20000) }
                }

                // 주문 상태가 CANCELLED로 변경되었는지 확인
                val updatedOrder = orderRepository.findById(order.id).get()
                assertThat(updatedOrder.status).isEqualTo(OrderStatus.CANCELLED)

                // 재고가 복구되었는지 확인
                val updatedProduct = productRepository.findById(product.id).get()
                assertThat(updatedProduct.stock).isEqualTo(100)
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        inner class FailTest {

            @Test
            @DisplayName("존재하지 않는 주문 ID면 404 에러를 반환한다")
            fun `should return 404 when order does not exist`() {
                // Given
                val nonExistentId = 999L

                // When & Then
                mockMvc.post("/api/v1/admin/orders/$nonExistentId/cancel") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isNotFound() }
                }
            }

            @Test
            @DisplayName("이미 취소된 주문이면 400 에러를 반환한다")
            fun `should return 400 when order is already cancelled`() {
                // Given
                val user = userRepository.save(User(nickname = "testuser", currentPoint = 0))
                val product = productRepository.save(
                    Product(name = "포인트 10,000원", price = 10000, stock = 100, status = ProductStatus.ACTIVE)
                )
                val order = orderRepository.save(
                    Order(
                        user = user,
                        product = product,
                        quantity = 1,
                        totalPrice = 10000,
                        status = OrderStatus.CANCELLED
                    )
                )

                // When & Then
                mockMvc.post("/api/v1/admin/orders/${order.id}/cancel") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }
}
