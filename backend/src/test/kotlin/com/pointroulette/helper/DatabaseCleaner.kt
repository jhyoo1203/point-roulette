package com.pointroulette.helper

import com.pointroulette.domain.budget.DailyBudgetRepository
import com.pointroulette.domain.order.OrderRepository
import com.pointroulette.domain.point.PointHistoryRepository
import com.pointroulette.domain.point.PointRepository
import com.pointroulette.domain.product.ProductRepository
import com.pointroulette.domain.roulette.RouletteHistoryRepository
import com.pointroulette.domain.user.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 테스트 데이터베이스 정리 유틸리티
 */
@Component
class DatabaseCleaner(
    private val orderRepository: OrderRepository,
    private val rouletteHistoryRepository: RouletteHistoryRepository,
    private val pointHistoryRepository: PointHistoryRepository,
    private val pointRepository: PointRepository,
    private val productRepository: ProductRepository,
    private val dailyBudgetRepository: DailyBudgetRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun clear() {
        orderRepository.deleteAll()
        rouletteHistoryRepository.deleteAll()
        pointHistoryRepository.deleteAll()
        pointRepository.deleteAll()
        productRepository.deleteAll()
        dailyBudgetRepository.deleteAll()
        userRepository.deleteAll()
    }
}
