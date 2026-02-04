package com.pointroulette.domain.product

import com.pointroulette.domain.common.BaseEntity
import jakarta.persistence.*

/**
 * 상품 엔티티
 */
@Entity
@Table(
    name = "products",
    indexes = [
        Index(name = "idx_products_status", columnList = "status")
    ]
)
class Product(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var price: Int,

    @Column(nullable = false)
    var stock: Int,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ProductStatus = ProductStatus.ACTIVE
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
