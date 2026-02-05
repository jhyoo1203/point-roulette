package com.pointroulette.helper

import com.pointroulette.domain.order.Order
import com.pointroulette.domain.order.OrderStatus
import com.pointroulette.domain.product.Product
import com.pointroulette.domain.product.ProductStatus
import com.pointroulette.domain.user.User
import java.lang.reflect.Field

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
     * 리플렉션을 사용하여 엔티티의 id 필드를 설정합니다.
     */
    private fun setId(entity: Any, id: Long) {
        val idField: Field = entity.javaClass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(entity, id)
    }
}