package com.pointroulette.presentation.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.pointroulette.application.order.dto.OrderCreateRequest
import com.pointroulette.domain.order.OrderRepository
import com.pointroulette.domain.point.Point
import com.pointroulette.domain.point.PointRepository
import com.pointroulette.domain.point.PointSourceType
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
import java.time.LocalDateTime

/**
 * ProductController 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DisplayName("ProductController 통합 테스트")
class ProductControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var pointRepository: PointRepository

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var databaseCleaner: DatabaseCleaner

    @BeforeEach
    fun setUp() {
        databaseCleaner.clear()
    }

    @Nested
    @DisplayName("GET /api/v1/products")
    inner class GetProductsApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("ACTIVE 상품만 조회된다")
            fun `should return only active products`() {
                // Given
                productRepository.save(
                    Product(name = "활성상품1", price = 10000, stock = 100, status = ProductStatus.ACTIVE)
                )
                productRepository.save(
                    Product(name = "활성상품2", price = 20000, stock = 50, status = ProductStatus.ACTIVE)
                )
                productRepository.save(
                    Product(name = "비활성상품", price = 30000, stock = 30, status = ProductStatus.INACTIVE)
                )

                // When & Then
                mockMvc.get("/api/v1/products") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(2) }
                    jsonPath("$.data.content[0].status") { value("ACTIVE") }
                    jsonPath("$.data.content[1].status") { value("ACTIVE") }
                }
            }

            @Test
            @DisplayName("페이징 파라미터를 지정하여 조회할 수 있다")
            fun `should return products with pagination parameters`() {
                // Given
                for (i in 1..5) {
                    productRepository.save(
                        Product(name = "상품$i", price = 10000 * i, stock = 100, status = ProductStatus.ACTIVE)
                    )
                }

                // When & Then
                mockMvc.get("/api/v1/products?page=0&size=3") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(3) }
                    jsonPath("$.data.totalElements") { value(5) }
                    jsonPath("$.data.pageSize") { value(3) }
                }
            }

            @Test
            @DisplayName("상품이 없어도 빈 페이징 응답을 반환한다")
            fun `should return empty pagination response when no products`() {
                // When & Then
                mockMvc.get("/api/v1/products") {
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
    @DisplayName("POST /api/v1/products/purchase/{userId}")
    inner class PurchaseProductApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("유효한 요청이면 201 응답과 주문 정보를 반환한다")
            fun `should return 201 with order response when request is valid`() {
                // Given
                val user = userRepository.save(User(nickname = "testuser", currentPoint = 20000))
                val product = productRepository.save(
                    Product(name = "포인트 10,000원", price = 10000, stock = 100, status = ProductStatus.ACTIVE)
                )
                pointRepository.save(
                    Point(
                        user = user,
                        initialAmount = 20000,
                        expiresAt = LocalDateTime.now().plusDays(30),
                        sourceType = PointSourceType.ROULETTE,
                        sourceId = 1L
                    )
                )

                val request = OrderCreateRequest(
                    productId = product.id,
                    quantity = 2
                )

                // When & Then
                mockMvc.post("/api/v1/products/purchase/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.httpStatus") { value(201) }
                    jsonPath("$.data.userId") { value(user.id) }
                    jsonPath("$.data.productId") { value(product.id) }
                    jsonPath("$.data.productName") { value("포인트 10,000원") }
                    jsonPath("$.data.quantity") { value(2) }
                    jsonPath("$.data.totalPrice") { value(20000) }
                    jsonPath("$.data.status") { value("COMPLETED") }
                }

                // 재고가 차감되었는지 확인
                val updatedProduct = productRepository.findById(product.id).get()
                assertThat(updatedProduct.stock).isEqualTo(98)

                // 포인트가 차감되었는지 확인
                val updatedUser = userRepository.findById(user.id).get()
                assertThat(updatedUser.currentPoint).isEqualTo(0)
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        inner class FailTest {

            @Test
            @DisplayName("상품을 찾을 수 없으면 404 에러를 반환한다")
            fun `should return 404 when product not found`() {
                // Given
                val user = userRepository.save(User(nickname = "testuser", currentPoint = 20000))
                val nonExistentProductId = 999L

                val request = OrderCreateRequest(
                    productId = nonExistentProductId,
                    quantity = 1
                )

                // When & Then
                mockMvc.post("/api/v1/products/purchase/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isNotFound() }
                }
            }

            @Test
            @DisplayName("재고가 부족하면 400 에러를 반환한다")
            fun `should return 400 when stock is insufficient`() {
                // Given
                val user = userRepository.save(User(nickname = "testuser", currentPoint = 100000))
                val product = productRepository.save(
                    Product(name = "재고부족 상품", price = 10000, stock = 5, status = ProductStatus.ACTIVE)
                )
                pointRepository.save(
                    Point(
                        user = user,
                        initialAmount = 100000,
                        expiresAt = LocalDateTime.now().plusDays(30),
                        sourceType = PointSourceType.ROULETTE,
                        sourceId = 1L
                    )
                )

                val request = OrderCreateRequest(
                    productId = product.id,
                    quantity = 10  // 재고보다 많음
                )

                // When & Then
                mockMvc.post("/api/v1/products/purchase/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("포인트가 부족하면 422 에러를 반환한다")
            fun `should return 422 when points are insufficient`() {
                // Given
                val user = userRepository.save(User(nickname = "testuser", currentPoint = 5000))
                val product = productRepository.save(
                    Product(name = "포인트 10,000원", price = 10000, stock = 100, status = ProductStatus.ACTIVE)
                )
                pointRepository.save(
                    Point(
                        user = user,
                        initialAmount = 5000,
                        expiresAt = LocalDateTime.now().plusDays(30),
                        sourceType = PointSourceType.ROULETTE,
                        sourceId = 1L
                    )
                )

                val request = OrderCreateRequest(
                    productId = product.id,
                    quantity = 1  // 10000원 필요
                )

                // When & Then
                mockMvc.post("/api/v1/products/purchase/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isUnprocessableEntity() }
                }
            }

            @Test
            @DisplayName("비활성 상품이면 400 에러를 반환한다")
            fun `should return 400 when product is inactive`() {
                // Given
                val user = userRepository.save(User(nickname = "testuser", currentPoint = 20000))
                val product = productRepository.save(
                    Product(name = "비활성 상품", price = 10000, stock = 100, status = ProductStatus.INACTIVE)
                )

                val request = OrderCreateRequest(
                    productId = product.id,
                    quantity = 1
                )

                // When & Then
                mockMvc.post("/api/v1/products/purchase/${user.id}") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }
}
