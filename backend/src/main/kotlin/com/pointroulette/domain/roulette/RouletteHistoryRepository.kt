package com.pointroulette.domain.roulette

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface RouletteHistoryRepository : JpaRepository<RouletteHistory, Long> {
    fun existsByUserIdAndParticipatedDate(userId: Long, participatedDate: LocalDate): Boolean

    fun findByUserIdAndParticipatedDate(@Param("userId") userId: Long,
                                        @Param("participatedDate") participatedDate: LocalDate
    ): RouletteHistory?

    /**
     * 날짜 범위로 룰렛 참여 이력 조회
     * @param startDate 시작일
     * @param endDate 종료일
     * @param pageable 페이징 정보
     * @return 룰렛 참여 이력 페이지
     */
    @Query("""
        SELECT rh FROM RouletteHistory rh
        JOIN FETCH rh.user
        WHERE rh.participatedDate BETWEEN :startDate AND :endDate
    """)
    fun findAllByParticipatedDateBetweenWithUser(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        pageable: Pageable
    ): Page<RouletteHistory>
}
