package com.pointroulette.domain.order

import org.springframework.data.jpa.repository.JpaRepository

/**
 * 주문 Repository
 */
interface OrderRepository : JpaRepository<Order, Long>