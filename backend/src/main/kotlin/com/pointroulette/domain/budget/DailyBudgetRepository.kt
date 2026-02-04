package com.pointroulette.domain.budget

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DailyBudgetRepository : JpaRepository<DailyBudget, Long> {
    fun findByBudgetDateBetween(startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<DailyBudget>
    fun existsByBudgetDate(budgetDate: LocalDate): Boolean
}
