package com.pointroulette.domain.roulette

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface RouletteHistoryRepository : JpaRepository<RouletteHistory, Long> {
    fun existsByUserIdAndParticipatedDate(userId: Long, participatedDate: LocalDate): Boolean

    fun findByUserIdAndParticipatedDate(@Param("userId") userId: Long,
                                        @Param("participatedDate") participatedDate: LocalDate
    ): RouletteHistory?
}
