package com.pointroulette.application.product

import com.pointroulette.application.product.dto.ProductCreateRequest
import com.pointroulette.application.product.dto.ProductResponse
import com.pointroulette.application.product.dto.ProductSearchRequest
import com.pointroulette.application.product.dto.ProductUpdateRequest
import com.pointroulette.common.model.PaginationResponse
import com.pointroulette.domain.product.Product
import com.pointroulette.domain.product.ProductRepository
import com.pointroulette.domain.product.ProductStatus
import com.pointroulette.presentation.exception.ResourceNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    @Transactional
    fun createProduct(request: ProductCreateRequest): ProductResponse {
        val product = Product(
            name = request.name,
            price = request.price,
            stock = request.stock,
            description = request.description,
            status = ProductStatus.ACTIVE
        )

        val savedProduct = productRepository.save(product)
        return ProductResponse.from(savedProduct)
    }

    @Transactional(readOnly = true)
    fun getProduct(id: Long): ProductResponse {
        val product = productRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("상품을 찾을 수 없습니다. (ID: $id)")

        return ProductResponse.from(product)
    }

    @Transactional(readOnly = true)
    fun getProducts(searchRequest: ProductSearchRequest): PaginationResponse<ProductResponse> {
        val pageable = searchRequest.toPageable()
        val page = if (searchRequest.status != null) {
            productRepository.findByStatus(searchRequest.status, pageable)
        } else {
            productRepository.findAll(pageable)
        }

        return PaginationResponse.from(page, ProductResponse::from)
    }

    @Transactional
    fun updateProduct(id: Long, request: ProductUpdateRequest): ProductResponse {
        val product = productRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("상품을 찾을 수 없습니다. (ID: $id)")

        request.name?.let { product.name = it }
        request.price?.let { product.price = it }
        request.stock?.let { product.stock = it }
        request.description?.let { product.description = it }

        return ProductResponse.from(product)
    }

    @Transactional
    fun deleteProduct(id: Long) {
        val product = productRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("상품을 찾을 수 없습니다. (ID: $id)")

        product.status = ProductStatus.INACTIVE
    }
}
