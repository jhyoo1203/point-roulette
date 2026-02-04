package com.pointroulette.domain.budget

import com.pointroulette.domain.common.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

/**
 * 일일 예산 엔티티
 * - 낙관적 락(version)을 통한 동시성 제어
 */
@Entity
@Table(
    name = "daily_budgets",
    indexes = [
        Index(name = "uk_daily_budgets_budget_date", columnList = "budget_date", unique = true)
    ]
)
class DailyBudget(
    @Column(name = "budget_date", nullable = false, unique = true)
    var budgetDate: LocalDate,

    @Column(name = "total_amount", nullable = false)
    var totalAmount: Int = 100_000,

    @Column(name = "remaining_amount", nullable = false)
    var remainingAmount: Int = totalAmount
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Version
    @Column(nullable = false)
    var version: Long = 0
}
