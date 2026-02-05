package com.pointroulette.presentation.admin.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.pointroulette.application.product.dto.ProductCreateRequest
import com.pointroulette.application.product.dto.ProductUpdateRequest
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
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * AdminProductController 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DisplayName("AdminProductController 통합 테스트")
class AdminProductControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var databaseCleaner: DatabaseCleaner

    @BeforeEach
    fun setUp() {
        databaseCleaner.clear()
    }

    @Nested
    @DisplayName("POST /api/v1/admin/products")
    inner class CreateProductApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("유효한 요청이면 201 응답과 생성된 상품 정보를 반환한다")
            fun `should return 201 with product response when request is valid`() {
                // Given
                val request = ProductCreateRequest(
                    name = "포인트 10,000원",
                    price = 10000,
                    stock = 100,
                    description = "10,000 포인트 상품"
                )

                // When & Then
                mockMvc.post("/api/v1/admin/products") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.httpStatus") { value(201) }
                    jsonPath("$.data.name") { value("포인트 10,000원") }
                    jsonPath("$.data.price") { value(10000) }
                    jsonPath("$.data.stock") { value(100) }
                    jsonPath("$.data.description") { value("10,000 포인트 상품") }
                    jsonPath("$.data.status") { value("ACTIVE") }
                    jsonPath("$.data.id") { exists() }
                    jsonPath("$.data.createdAt") { exists() }
                    jsonPath("$.data.updatedAt") { exists() }
                }

                // 데이터베이스에 상품이 생성되었는지 확인
                assertThat(productRepository.count()).isEqualTo(1)
                val savedProduct = productRepository.findAll()[0]
                assertThat(savedProduct.name).isEqualTo("포인트 10,000원")
                assertThat(savedProduct.price).isEqualTo(10000)
                assertThat(savedProduct.stock).isEqualTo(100)
                assertThat(savedProduct.status).isEqualTo(ProductStatus.ACTIVE)
            }

            @Test
            @DisplayName("설명 없이도 상품을 생성할 수 있다")
            fun `should create product without description`() {
                // Given
                val request = ProductCreateRequest(
                    name = "포인트 5,000원",
                    price = 5000,
                    stock = 50,
                    description = null
                )

                // When & Then
                mockMvc.post("/api/v1/admin/products") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.httpStatus") { value(201) }
                    jsonPath("$.data.name") { value("포인트 5,000원") }
                    jsonPath("$.data.description") { doesNotExist() }
                }
            }
        }

        @Nested
        @DisplayName("실패 케이스 - 유효성 검증")
        inner class ValidationFailTest {

            @Test
            @DisplayName("상품명이 비어있으면 400 에러를 반환한다")
            fun `should return 400 when name is blank`() {
                // Given
                val request = mapOf(
                    "name" to "",
                    "price" to 10000,
                    "stock" to 100
                )

                // When & Then
                mockMvc.post("/api/v1/admin/products") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("상품명이 100자를 초과하면 400 에러를 반환한다")
            fun `should return 400 when name is too long`() {
                // Given
                val request = ProductCreateRequest(
                    name = "A".repeat(101),
                    price = 10000,
                    stock = 100
                )

                // When & Then
                mockMvc.post("/api/v1/admin/products") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("가격이 음수면 400 에러를 반환한다")
            fun `should return 400 when price is negative`() {
                // Given
                val request = ProductCreateRequest(
                    name = "포인트 10,000원",
                    price = -1000,
                    stock = 100
                )

                // When & Then
                mockMvc.post("/api/v1/admin/products") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("재고가 음수면 400 에러를 반환한다")
            fun `should return 400 when stock is negative`() {
                // Given
                val request = ProductCreateRequest(
                    name = "포인트 10,000원",
                    price = 10000,
                    stock = -50
                )

                // When & Then
                mockMvc.post("/api/v1/admin/products") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("설명이 500자를 초과하면 400 에러를 반환한다")
            fun `should return 400 when description is too long`() {
                // Given
                val request = ProductCreateRequest(
                    name = "포인트 10,000원",
                    price = 10000,
                    stock = 100,
                    description = "A".repeat(501)
                )

                // When & Then
                mockMvc.post("/api/v1/admin/products") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }

    @Nested
    @DisplayName("GET /api/v1/admin/products/{id}")
    inner class GetProductApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("존재하는 상품 ID면 200 응답과 상품 정보를 반환한다")
            fun `should return 200 with product response when product exists`() {
                // Given
                val product = productRepository.save(
                    Product(
                        name = "포인트 10,000원",
                        price = 10000,
                        stock = 100,
                        description = "10,000 포인트 상품",
                        status = ProductStatus.ACTIVE
                    )
                )

                // When & Then
                mockMvc.get("/api/v1/admin/products/${product.id}") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.id") { value(product.id) }
                    jsonPath("$.data.name") { value("포인트 10,000원") }
                    jsonPath("$.data.price") { value(10000) }
                    jsonPath("$.data.stock") { value(100) }
                    jsonPath("$.data.status") { value("ACTIVE") }
                }
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        inner class FailTest {

            @Test
            @DisplayName("존재하지 않는 상품 ID면 404 에러를 반환한다")
            fun `should return 404 when product does not exist`() {
                // Given
                val nonExistentId = 999L

                // When & Then
                mockMvc.get("/api/v1/admin/products/$nonExistentId") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isNotFound() }
                }
            }
        }
    }

    @Nested
    @DisplayName("GET /api/v1/admin/products")
    inner class GetProductsApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("페이징 파라미터 없이 조회하면 기본값으로 전체 상품을 반환한다")
            fun `should return all products with default pagination`() {
                // Given
                productRepository.save(
                    Product(name = "상품1", price = 10000, stock = 100, status = ProductStatus.ACTIVE)
                )
                productRepository.save(
                    Product(name = "상품2", price = 20000, stock = 50, status = ProductStatus.ACTIVE)
                )

                // When & Then
                mockMvc.get("/api/v1/admin/products") {
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
            @DisplayName("상태 필터로 ACTIVE 상품만 조회할 수 있다")
            fun `should return only active products when status filter is ACTIVE`() {
                // Given
                productRepository.save(
                    Product(name = "활성상품", price = 10000, stock = 100, status = ProductStatus.ACTIVE)
                )
                productRepository.save(
                    Product(name = "비활성상품", price = 20000, stock = 50, status = ProductStatus.INACTIVE)
                )

                // When & Then
                mockMvc.get("/api/v1/admin/products?status=ACTIVE") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(1) }
                    jsonPath("$.data.content[0].name") { value("활성상품") }
                    jsonPath("$.data.content[0].status") { value("ACTIVE") }
                }
            }

            @Test
            @DisplayName("페이지 크기를 지정하여 조회할 수 있다")
            fun `should return products with custom page size`() {
                // Given
                for (i in 1..5) {
                    productRepository.save(
                        Product(name = "상품$i", price = 10000 * i, stock = 100, status = ProductStatus.ACTIVE)
                    )
                }

                // When & Then
                mockMvc.get("/api/v1/admin/products?page=0&size=3") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(3) }
                    jsonPath("$.data.totalElements") { value(5) }
                    jsonPath("$.data.pageSize") { value(3) }
                    jsonPath("$.data.totalPages") { value(2) }
                }
            }

            @Test
            @DisplayName("정렬 기준을 지정하여 조회할 수 있다")
            fun `should return products with custom sort`() {
                // Given
                productRepository.save(
                    Product(name = "상품A", price = 30000, stock = 100, status = ProductStatus.ACTIVE)
                )
                productRepository.save(
                    Product(name = "상품B", price = 10000, stock = 100, status = ProductStatus.ACTIVE)
                )
                productRepository.save(
                    Product(name = "상품C", price = 20000, stock = 100, status = ProductStatus.ACTIVE)
                )

                // When & Then
                mockMvc.get("/api/v1/admin/products?sort=price,asc") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content[0].price") { value(10000) }
                    jsonPath("$.data.content[1].price") { value(20000) }
                    jsonPath("$.data.content[2].price") { value(30000) }
                }
            }

            @Test
            @DisplayName("상품이 없어도 빈 페이징 응답을 반환한다")
            fun `should return empty pagination response when no products`() {
                // When & Then
                mockMvc.get("/api/v1/admin/products") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.content.length()") { value(0) }
                    jsonPath("$.data.totalElements") { value(0) }
                }
            }
        }

        @Nested
        @DisplayName("실패 케이스 - 유효성 검증")
        inner class ValidationFailTest {

            @Test
            @DisplayName("페이지 번호가 음수면 400 에러를 반환한다")
            fun `should return 400 when page is negative`() {
                // When & Then
                mockMvc.get("/api/v1/admin/products?page=-1") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("페이지 크기가 0 이하면 400 에러를 반환한다")
            fun `should return 400 when size is zero or negative`() {
                // When & Then
                mockMvc.get("/api/v1/admin/products?size=0") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("페이지 크기가 100을 초과하면 400 에러를 반환한다")
            fun `should return 400 when size exceeds 100`() {
                // When & Then
                mockMvc.get("/api/v1/admin/products?size=101") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/admin/products/{id}")
    inner class UpdateProductApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("모든 필드를 수정하면 200 응답과 수정된 상품 정보를 반환한다")
            fun `should return 200 with updated product when all fields are updated`() {
                // Given
                val product = productRepository.save(
                    Product(
                        name = "원래 상품명",
                        price = 10000,
                        stock = 100,
                        description = "원래 설명",
                        status = ProductStatus.ACTIVE
                    )
                )

                val request = ProductUpdateRequest(
                    name = "수정된 상품명",
                    price = 15000,
                    stock = 150,
                    description = "수정된 설명"
                )

                // When & Then
                mockMvc.put("/api/v1/admin/products/${product.id}") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.id") { value(product.id) }
                    jsonPath("$.data.name") { value("수정된 상품명") }
                    jsonPath("$.data.price") { value(15000) }
                    jsonPath("$.data.stock") { value(150) }
                    jsonPath("$.data.description") { value("수정된 설명") }
                }
            }

            @Test
            @DisplayName("일부 필드만 수정해도 정상 동작한다")
            fun `should return 200 when updating only some fields`() {
                // Given
                val product = productRepository.save(
                    Product(
                        name = "원래 상품명",
                        price = 10000,
                        stock = 100,
                        description = "원래 설명",
                        status = ProductStatus.ACTIVE
                    )
                )

                val request = ProductUpdateRequest(
                    name = "수정된 상품명",
                    price = null,
                    stock = null,
                    description = null
                )

                // When & Then
                mockMvc.put("/api/v1/admin/products/${product.id}") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.name") { value("수정된 상품명") }
                    jsonPath("$.data.price") { value(10000) } // 변경 안됨
                    jsonPath("$.data.stock") { value(100) } // 변경 안됨
                    jsonPath("$.data.description") { value("원래 설명") } // 변경 안됨
                }
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        inner class FailTest {

            @Test
            @DisplayName("존재하지 않는 상품 ID면 404 에러를 반환한다")
            fun `should return 404 when product does not exist`() {
                // Given
                val nonExistentId = 999L
                val request = ProductUpdateRequest(
                    name = "수정된 상품명",
                    price = 15000,
                    stock = 150,
                    description = "수정된 설명"
                )

                // When & Then
                mockMvc.put("/api/v1/admin/products/$nonExistentId") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isNotFound() }
                }
            }

            @Test
            @DisplayName("상품명이 100자를 초과하면 400 에러를 반환한다")
            fun `should return 400 when name is too long`() {
                // Given
                val product = productRepository.save(
                    Product(name = "상품", price = 10000, stock = 100, status = ProductStatus.ACTIVE)
                )

                val request = ProductUpdateRequest(
                    name = "A".repeat(101),
                    price = null,
                    stock = null,
                    description = null
                )

                // When & Then
                mockMvc.put("/api/v1/admin/products/${product.id}") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("가격이 음수면 400 에러를 반환한다")
            fun `should return 400 when price is negative`() {
                // Given
                val product = productRepository.save(
                    Product(name = "상품", price = 10000, stock = 100, status = ProductStatus.ACTIVE)
                )

                val request = ProductUpdateRequest(
                    name = null,
                    price = -1000,
                    stock = null,
                    description = null
                )

                // When & Then
                mockMvc.put("/api/v1/admin/products/${product.id}") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/admin/products/{id}")
    inner class DeleteProductApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("존재하는 상품 ID면 200 응답을 반환하고 상품 상태를 INACTIVE로 변경한다")
            fun `should return 200 and change status to INACTIVE when product exists`() {
                // Given
                val product = productRepository.save(
                    Product(
                        name = "포인트 10,000원",
                        price = 10000,
                        stock = 100,
                        description = "10,000 포인트 상품",
                        status = ProductStatus.ACTIVE
                    )
                )

                // When & Then
                mockMvc.delete("/api/v1/admin/products/${product.id}") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                }

                // 상품 상태가 INACTIVE로 변경되었는지 확인
                val updatedProduct = productRepository.findById(product.id).get()
                assertThat(updatedProduct.status).isEqualTo(ProductStatus.INACTIVE)
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        inner class FailTest {

            @Test
            @DisplayName("존재하지 않는 상품 ID면 404 에러를 반환한다")
            fun `should return 404 when product does not exist`() {
                // Given
                val nonExistentId = 999L

                // When & Then
                mockMvc.delete("/api/v1/admin/products/$nonExistentId") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isNotFound() }
                }
            }
        }
    }

    @Nested
    @DisplayName("POST /api/v1/admin/products/orders/{orderId}/cancel")
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
                mockMvc.post("/api/v1/admin/products/orders/${order.id}/cancel") {
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
                mockMvc.post("/api/v1/admin/products/orders/$nonExistentId/cancel") {
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
                mockMvc.post("/api/v1/admin/products/orders/${order.id}/cancel") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }
}
