package com.pointroulette.domain.order

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * 주문 Repository
 */
interface OrderRepository : JpaRepository<Order, Long> {

    /**
     * 사용자별 주문 내역 조회 (페이징)
     */
    @Query("""
        SELECT o FROM Order o
        JOIN FETCH o.product
        WHERE o.user.id = :userId
        ORDER BY o.createdAt DESC
    """,
    countQuery = """
        SELECT COUNT(o) FROM Order o
        WHERE o.user.id = :userId
    """)
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<Order>
}
