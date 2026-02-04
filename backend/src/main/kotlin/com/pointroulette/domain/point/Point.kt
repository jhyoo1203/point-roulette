package com.pointroulette.domain.point

import com.pointroulette.domain.common.BaseEntity
import com.pointroulette.domain.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 포인트 지갑 엔티티
 * - FIFO 방식으로 관리 (오래된 포인트부터 사용)
 */
@Entity
@Table(
    name = "points",
    indexes = [
        Index(name = "idx_points_user_id", columnList = "user_id"),
        Index(name = "idx_points_status", columnList = "status"),
        Index(name = "idx_points_expires_at", columnList = "expires_at"),
        Index(name = "idx_points_source", columnList = "source_type,source_id")
    ]
)
class Point(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "initial_amount", nullable = false)
    val initialAmount: Int,

    @Column(name = "remaining_amount", nullable = false)
    var remainingAmount: Int = initialAmount,

    @Column(name = "earned_at", nullable = false)
    val earnedAt: LocalDateTime,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    val sourceType: PointSourceType,

    @Column(name = "source_id", nullable = false)
    val sourceId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PointStatus = PointStatus.ACTIVE
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
