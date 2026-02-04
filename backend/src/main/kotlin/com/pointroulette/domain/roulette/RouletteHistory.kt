package com.pointroulette.domain.roulette

import com.pointroulette.domain.budget.DailyBudget
import com.pointroulette.domain.common.BaseEntity
import com.pointroulette.domain.user.User
import jakarta.persistence.*
import java.time.LocalDate

/**
 * 룰렛 참여 이력 엔티티
 */
@Entity
@Table(
    name = "roulette_histories",
    indexes = [
        Index(name = "uk_roulette_histories_user_date", columnList = "user_id,participated_date", unique = true),
        Index(name = "idx_roulette_histories_user_id", columnList = "user_id"),
        Index(name = "idx_roulette_histories_participated_date", columnList = "participated_date"),
        Index(name = "idx_roulette_histories_daily_budget_id", columnList = "daily_budget_id")
    ]
)
class RouletteHistory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "participated_date", nullable = false)
    val participatedDate: LocalDate,

    @Column(name = "won_amount", nullable = false)
    val wonAmount: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_budget_id", nullable = false)
    val dailyBudget: DailyBudget,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: RouletteStatus = RouletteStatus.SUCCESS
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
