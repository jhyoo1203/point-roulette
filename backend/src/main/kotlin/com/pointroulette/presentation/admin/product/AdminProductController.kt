package com.pointroulette.presentation.admin.product

import com.pointroulette.application.product.ProductService
import com.pointroulette.application.product.dto.ProductCreateRequest
import com.pointroulette.application.product.dto.ProductResponse
import com.pointroulette.application.product.dto.ProductSearchRequest
import com.pointroulette.application.product.dto.ProductUpdateRequest
import com.pointroulette.common.model.PaginationResponse
import com.pointroulette.presentation.common.dto.ResponseData
import com.pointroulette.presentation.admin.product.swagger.AdminProductControllerDocs
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/products")
class AdminProductController(
    private val productService: ProductService
) : AdminProductControllerDocs {

    @PostMapping
    override fun createProduct(
        @Valid @RequestBody request: ProductCreateRequest
    ): ResponseEntity<ResponseData<ProductResponse>> {
        val response = productService.createProduct(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.of(HttpStatus.CREATED, response))
    }

    @GetMapping("/{id}")
    override fun getProduct(
        @PathVariable id: Long
    ): ResponseEntity<ResponseData<ProductResponse>> {
        val response = productService.getProduct(id)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }

    @GetMapping
    override fun getProducts(
        @ParameterObject @Valid @ModelAttribute searchRequest: ProductSearchRequest
    ): ResponseEntity<ResponseData<PaginationResponse<ProductResponse>>> {
        val response = productService.getProducts(searchRequest)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }

    @PutMapping("/{id}")
    override fun updateProduct(
        @PathVariable id: Long,
        @Valid @RequestBody request: ProductUpdateRequest
    ): ResponseEntity<ResponseData<ProductResponse>> {
        val response = productService.updateProduct(id, request)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }

    @DeleteMapping("/{id}")
    override fun deleteProduct(
        @PathVariable id: Long
    ): ResponseEntity<ResponseData<Unit>> {
        productService.deleteProduct(id)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK))
    }
}
