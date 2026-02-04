package com.pointroulette.domain.order

import com.pointroulette.domain.common.BaseEntity
import com.pointroulette.domain.product.Product
import com.pointroulette.domain.user.User
import jakarta.persistence.*

/**
 * 주문 엔티티
 */
@Entity
@Table(
    name = "orders",
    indexes = [
        Index(name = "idx_orders_user_id", columnList = "user_id"),
        Index(name = "idx_orders_product_id", columnList = "product_id"),
        Index(name = "idx_orders_status", columnList = "status"),
        Index(name = "idx_orders_created_at", columnList = "created_at")
    ]
)
class Order(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(nullable = false)
    val quantity: Int,

    @Column(name = "total_price", nullable = false)
    val totalPrice: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.COMPLETED
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
