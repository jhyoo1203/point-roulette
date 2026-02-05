package com.pointroulette.domain.user

import com.pointroulette.domain.common.BaseEntity
import com.pointroulette.domain.point.InsufficientPointException
import jakarta.persistence.*

/**
 * 사용자 엔티티
 */
@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "uk_users_nickname", columnList = "nickname", unique = true)
    ]
)
class User(
    @Column(nullable = false, unique = true)
    var nickname: String,

    @Column(name = "current_point", nullable = false)
    var currentPoint: Int = 0
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    /**
     * 현재 포인트를 변경합니다.
     * - 양수: 포인트 증가 (적립, 환불 등)
     * - 음수: 포인트 감소 (사용, 만료 등)
     *
     * @param amount 변경할 포인트 (0이 아니어야 함)
     * @throws IllegalArgumentException amount가 0인 경우
     * @throws InsufficientPointException 포인트 부족으로 음수가 되는 경우
     */
    fun updateCurrentPoint(amount: Int) {
        require(amount != 0) { "변경할 포인트는 0이 아니어야 합니다" }

        val newPoint = this.currentPoint + amount

        // 포인트 부족 체크 (음수 방지)
        if (newPoint < 0) {
            throw InsufficientPointException(
                currentPoint = this.currentPoint,
                requestedAmount = amount
            )
        }

        this.currentPoint = newPoint
    }
}
