package com.pointroulette.domain.point

import com.pointroulette.domain.common.BaseEntity
import com.pointroulette.domain.user.User
import jakarta.persistence.*

/**
 * 포인트 변동 이력 엔티티
 */
@Entity
@Table(
    name = "point_histories",
    indexes = [
        Index(name = "idx_point_histories_user_id", columnList = "user_id"),
        Index(name = "idx_point_histories_point_id", columnList = "point_id"),
        Index(name = "idx_point_histories_created_at", columnList = "created_at"),
        Index(name = "idx_point_histories_reference", columnList = "reference_type,reference_id")
    ]
)
class PointHistory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id")
    val point: Point? = null,

    @Column(nullable = false)
    val amount: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    val transactionType: TransactionType,

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", nullable = false)
    val referenceType: ReferenceType,

    @Column(name = "reference_id", nullable = false)
    val referenceId: Long,

    @Column(name = "balance_after", nullable = false)
    val balanceAfter: Int
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
