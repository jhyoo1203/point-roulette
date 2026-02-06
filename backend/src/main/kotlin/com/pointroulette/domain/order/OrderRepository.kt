package com.pointroulette.domain.order

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

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

    /**
     * 관리자용 전체 주문 조회 (userId, status 필터 지원)
     */
    @Query("""
        SELECT o FROM Order o
        JOIN FETCH o.product
        WHERE (:userId IS NULL OR o.user.id = :userId)
          AND (:status IS NULL OR o.status = :status)
    """,
    countQuery = """
        SELECT COUNT(o) FROM Order o
        WHERE (:userId IS NULL OR o.user.id = :userId)
          AND (:status IS NULL OR o.status = :status)
    """)
    fun findAllWithFilters(
        @Param("userId") userId: Long?,
        @Param("status") status: OrderStatus?,
        pageable: Pageable
    ): Page<Order>
}
