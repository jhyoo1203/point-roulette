package com.pointroulette.helper

import com.pointroulette.domain.budget.DailyBudget
import com.pointroulette.domain.order.Order
import com.pointroulette.domain.order.OrderStatus
import com.pointroulette.domain.point.Point
import com.pointroulette.domain.point.PointSourceType
import com.pointroulette.domain.point.PointStatus
import com.pointroulette.domain.product.Product
import com.pointroulette.domain.product.ProductStatus
import com.pointroulette.domain.roulette.RouletteHistory
import com.pointroulette.domain.roulette.RouletteStatus
import com.pointroulette.domain.user.User
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 테스트용 엔티티 팩토리
 * - JPA 엔티티의 id 필드를 리플렉션으로 설정합니다.
 */
object TestEntityFactory {

    /**
     * ID가 설정된 테스트용 User 생성
     */
    fun createUser(
        id: Long,
        nickname: String,
        currentPoint: Int = 0
    ): User {
        val user = User(
            nickname = nickname,
            currentPoint = currentPoint
        )
        setId(user, id)
        return user
    }

    /**
     * ID가 설정된 테스트용 Product 생성
     */
    fun createProduct(
        id: Long,
        name: String,
        price: Int,
        stock: Int,
        description: String? = null,
        status: ProductStatus = ProductStatus.ACTIVE
    ): Product {
        val product = Product(
            name = name,
            price = price,
            stock = stock,
            description = description,
            status = status
        )
        setId(product, id)
        return product
    }

    /**
     * ID가 설정된 테스트용 Order 생성
     */
    fun createOrder(
        id: Long,
        user: User,
        product: Product,
        quantity: Int,
        totalPrice: Int,
        status: OrderStatus = OrderStatus.COMPLETED
    ): Order {
        val order = Order(
            user = user,
            product = product,
            quantity = quantity,
            totalPrice = totalPrice,
            status = status
        )
        setId(order, id)
        return order
    }

    /**
     * ID가 설정된 테스트용 DailyBudget 생성
     */
    fun createDailyBudget(
        id: Long,
        budgetDate: LocalDate,
        totalAmount: Int = 100_000,
        remainingAmount: Int = 100_000
    ): DailyBudget {
        val budget = DailyBudget(
            budgetDate = budgetDate,
            totalAmount = totalAmount,
            remainingAmount = remainingAmount
        )
        setId(budget, id)
        return budget
    }

    /**
     * ID가 설정된 테스트용 RouletteHistory 생성
     */
    fun createRouletteHistory(
        id: Long,
        user: User,
        participatedDate: LocalDate,
        wonAmount: Int,
        dailyBudget: DailyBudget,
        status: RouletteStatus = RouletteStatus.SUCCESS
    ): RouletteHistory {
        val history = RouletteHistory(
            user = user,
            participatedDate = participatedDate,
            wonAmount = wonAmount,
            dailyBudget = dailyBudget,
            status = status
        )
        setId(history, id)
        return history
    }

    /**
     * ID가 설정된 테스트용 Point 생성
     */
    fun createPoint(
        id: Long,
        user: User,
        initialAmount: Int,
        remainingAmount: Int = initialAmount,
        expiresAt: LocalDateTime,
        sourceType: PointSourceType,
        sourceId: Long,
        status: PointStatus = PointStatus.ACTIVE
    ): Point {
        val point = Point(
            user = user,
            initialAmount = initialAmount,
            expiresAt = expiresAt,
            sourceType = sourceType,
            sourceId = sourceId
        )
        point.remainingAmount = remainingAmount
        point.status = status
        setId(point, id)
        return point
    }

    /**
     * 리플렉션을 사용하여 엔티티의 id 필드를 설정합니다.
     */
    private fun setId(entity: Any, id: Long) {
        val idField: Field = entity.javaClass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(entity, id)
    }
}