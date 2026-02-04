package com.pointroulette.application.product

import com.pointroulette.application.product.dto.ProductCreateRequest
import com.pointroulette.application.product.dto.ProductSearchRequest
import com.pointroulette.application.product.dto.ProductUpdateRequest
import com.pointroulette.domain.product.Product
import com.pointroulette.domain.product.ProductRepository
import com.pointroulette.domain.product.ProductStatus
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
import org.springframework.data.domain.PageImpl
import java.util.Optional

/**
 * ProductService 단위 테스트
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("ProductService 테스트")
class ProductServiceTest {

    @Mock
    private lateinit var productRepository: ProductRepository

    @InjectMocks
    private lateinit var productService: ProductService

    @Nested
    @DisplayName("createProduct 메서드")
    inner class CreateProductTest {

        @Test
        @DisplayName("유효한 요청이면 상품을 생성하고 응답을 반환한다")
        fun `should create product and return response when request is valid`() {
            // Given
            val request = ProductCreateRequest(
                name = "포인트 10,000원",
                price = 10000,
                stock = 100,
                description = "10,000 포인트 상품"
            )

            val savedProduct = Product(
                name = request.name,
                price = request.price,
                stock = request.stock,
                description = request.description,
                status = ProductStatus.ACTIVE
            )

            given(productRepository.save(any())).willReturn(savedProduct)

            // When
            val response = productService.createProduct(request)

            // Then
            assertThat(response.name).isEqualTo(request.name)
            assertThat(response.price).isEqualTo(request.price)
            assertThat(response.stock).isEqualTo(request.stock)
            assertThat(response.description).isEqualTo(request.description)
            assertThat(response.status).isEqualTo(ProductStatus.ACTIVE)
            verify(productRepository).save(any())
        }

        @Test
        @DisplayName("설명이 없어도 상품을 생성한다")
        fun `should create product without description`() {
            // Given
            val request = ProductCreateRequest(
                name = "포인트 10,000원",
                price = 10000,
                stock = 100,
                description = null
            )

            val savedProduct = Product(
                name = request.name,
                price = request.price,
                stock = request.stock,
                description = null,
                status = ProductStatus.ACTIVE
            )

            given(productRepository.save(any())).willReturn(savedProduct)

            // When
            val response = productService.createProduct(request)

            // Then
            assertThat(response.name).isEqualTo(request.name)
            assertThat(response.description).isNull()
            verify(productRepository).save(any())
        }
    }

    @Nested
    @DisplayName("getProduct 메서드")
    inner class GetProductTest {

        @Test
        @DisplayName("존재하는 상품 ID면 상품 정보를 반환한다")
        fun `should return product response when product exists`() {
            // Given
            val productId = 1L
            val product = Product(
                name = "포인트 10,000원",
                price = 10000,
                stock = 100,
                description = "10,000 포인트 상품",
                status = ProductStatus.ACTIVE
            )

            given(productRepository.findById(productId)).willReturn(Optional.of(product))

            // When
            val response = productService.getProduct(productId)

            // Then
            assertThat(response.name).isEqualTo(product.name)
            assertThat(response.price).isEqualTo(product.price)
            assertThat(response.stock).isEqualTo(product.stock)
            assertThat(response.status).isEqualTo(ProductStatus.ACTIVE)
            verify(productRepository).findById(productId)
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID면 ResourceNotFoundException을 발생시킨다")
        fun `should throw ResourceNotFoundException when product does not exist`() {
            // Given
            val productId = 999L
            given(productRepository.findById(productId)).willReturn(Optional.empty())

            // When & Then
            assertThatThrownBy { productService.getProduct(productId) }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessageContaining("상품을 찾을 수 없습니다")

            verify(productRepository).findById(productId)
        }
    }

    @Nested
    @DisplayName("getProducts 메서드")
    inner class GetProductsTest {

        @Test
        @DisplayName("상태 필터 없이 조회하면 전체 상품을 페이징하여 반환한다")
        fun `should return all products when no status filter`() {
            // Given
            val searchRequest = ProductSearchRequest(
                page = 0,
                size = 10,
                sort = "updatedAt,desc",
                status = null
            )

            val products = listOf(
                Product(name = "상품1", price = 10000, stock = 100, status = ProductStatus.ACTIVE),
                Product(name = "상품2", price = 20000, stock = 50, status = ProductStatus.INACTIVE)
            )

            val pageable = searchRequest.toPageable()
            val page = PageImpl(products, pageable, products.size.toLong())

            given(productRepository.findAll(pageable)).willReturn(page)

            // When
            val response = productService.getProducts(searchRequest)

            // Then
            assertThat(response.content).hasSize(2)
            assertThat(response.totalElements).isEqualTo(2)
            assertThat(response.currentPage).isEqualTo(0)
            assertThat(response.pageSize).isEqualTo(10)
            verify(productRepository).findAll(pageable)
        }

        @Test
        @DisplayName("상태 필터가 있으면 해당 상태의 상품만 반환한다")
        fun `should return filtered products when status filter exists`() {
            // Given
            val searchRequest = ProductSearchRequest(
                page = 0,
                size = 10,
                sort = "updatedAt,desc",
                status = ProductStatus.ACTIVE
            )

            val products = listOf(
                Product(name = "활성상품1", price = 10000, stock = 100, status = ProductStatus.ACTIVE),
                Product(name = "활성상품2", price = 20000, stock = 50, status = ProductStatus.ACTIVE)
            )

            val pageable = searchRequest.toPageable()
            val page = PageImpl(products, pageable, products.size.toLong())

            given(productRepository.findByStatus(ProductStatus.ACTIVE, pageable)).willReturn(page)

            // When
            val response = productService.getProducts(searchRequest)

            // Then
            assertThat(response.content).hasSize(2)
            assertThat(response.content).allMatch { it.status == ProductStatus.ACTIVE }
            verify(productRepository).findByStatus(ProductStatus.ACTIVE, pageable)
        }

        @Test
        @DisplayName("빈 결과도 페이징 응답으로 반환한다")
        fun `should return empty pagination response when no products found`() {
            // Given
            val searchRequest = ProductSearchRequest(
                page = 0,
                size = 10,
                sort = "updatedAt,desc",
                status = null
            )

            val pageable = searchRequest.toPageable()
            val page = PageImpl<Product>(emptyList(), pageable, 0)

            given(productRepository.findAll(pageable)).willReturn(page)

            // When
            val response = productService.getProducts(searchRequest)

            // Then
            assertThat(response.content).isEmpty()
            assertThat(response.totalElements).isEqualTo(0)
            verify(productRepository).findAll(pageable)
        }
    }

    @Nested
    @DisplayName("updateProduct 메서드")
    inner class UpdateProductTest {

        @Test
        @DisplayName("존재하는 상품의 모든 필드를 수정하고 응답을 반환한다")
        fun `should update all fields when product exists`() {
            // Given
            val productId = 1L
            val request = ProductUpdateRequest(
                name = "수정된 상품명",
                price = 15000,
                stock = 150,
                description = "수정된 설명"
            )

            val product = Product(
                name = "원래 상품명",
                price = 10000,
                stock = 100,
                description = "원래 설명",
                status = ProductStatus.ACTIVE
            )

            given(productRepository.findById(productId)).willReturn(Optional.of(product))

            // When
            val response = productService.updateProduct(productId, request)

            // Then
            assertThat(response.name).isEqualTo(request.name)
            assertThat(response.price).isEqualTo(request.price)
            assertThat(response.stock).isEqualTo(request.stock)
            assertThat(response.description).isEqualTo(request.description)
            verify(productRepository).findById(productId)
        }

        @Test
        @DisplayName("일부 필드만 수정해도 정상 동작한다")
        fun `should update only provided fields`() {
            // Given
            val productId = 1L
            val request = ProductUpdateRequest(
                name = "수정된 상품명",
                price = null,
                stock = null,
                description = null
            )

            val product = Product(
                name = "원래 상품명",
                price = 10000,
                stock = 100,
                description = "원래 설명",
                status = ProductStatus.ACTIVE
            )

            given(productRepository.findById(productId)).willReturn(Optional.of(product))

            // When
            val response = productService.updateProduct(productId, request)

            // Then
            assertThat(response.name).isEqualTo(request.name)
            assertThat(response.price).isEqualTo(10000) // 변경 안됨
            assertThat(response.stock).isEqualTo(100) // 변경 안됨
            assertThat(response.description).isEqualTo("원래 설명") // 변경 안됨
            verify(productRepository).findById(productId)
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID면 ResourceNotFoundException을 발생시킨다")
        fun `should throw ResourceNotFoundException when product does not exist`() {
            // Given
            val productId = 999L
            val request = ProductUpdateRequest(
                name = "수정된 상품명",
                price = 15000,
                stock = 150,
                description = "수정된 설명"
            )

            given(productRepository.findById(productId)).willReturn(Optional.empty())

            // When & Then
            assertThatThrownBy { productService.updateProduct(productId, request) }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessageContaining("상품을 찾을 수 없습니다")

            verify(productRepository).findById(productId)
        }
    }

    @Nested
    @DisplayName("deleteProduct 메서드")
    inner class DeleteProductTest {

        @Test
        @DisplayName("존재하는 상품의 상태를 INACTIVE로 변경한다")
        fun `should change status to INACTIVE when product exists`() {
            // Given
            val productId = 1L
            val product = Product(
                name = "포인트 10,000원",
                price = 10000,
                stock = 100,
                description = "10,000 포인트 상품",
                status = ProductStatus.ACTIVE
            )

            given(productRepository.findById(productId)).willReturn(Optional.of(product))

            // When
            productService.deleteProduct(productId)

            // Then
            assertThat(product.status).isEqualTo(ProductStatus.INACTIVE)
            verify(productRepository).findById(productId)
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID면 ResourceNotFoundException을 발생시킨다")
        fun `should throw ResourceNotFoundException when product does not exist`() {
            // Given
            val productId = 999L
            given(productRepository.findById(productId)).willReturn(Optional.empty())

            // When & Then
            assertThatThrownBy { productService.deleteProduct(productId) }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessageContaining("상품을 찾을 수 없습니다")

            verify(productRepository).findById(productId)
        }
    }
}
