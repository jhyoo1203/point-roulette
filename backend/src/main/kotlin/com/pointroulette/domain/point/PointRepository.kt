package com.pointroulette.domain.point

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

/**
 * 포인트 Repository
 */
interface PointRepository : JpaRepository<Point, Long> {

    /**
     * FIFO 방식으로 사용 가능한 포인트 조회
     * - ACTIVE 상태
     * - 만료일이 오래된 순서대로 정렬
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT p FROM Point p
        JOIN FETCH p.user
        WHERE p.user.id = :userId
          AND p.status = 'ACTIVE'
          AND p.remainingAmount > 0
        ORDER BY p.expiresAt ASC, p.id ASC
    """)
    fun findAvailablePointsByUserId(userId: Long): List<Point>

    /**
     * 특정 소스로 획득한 포인트 조회
     */
    fun findByUserIdAndSourceTypeAndSourceId(
        userId: Long,
        sourceType: PointSourceType,
        sourceId: Long
    ): Point?

    /**
     * 사용자의 모든 포인트 조회 (만료일 오름차순)
     * - 포인트 잔액 조회용
     */
    @Query("""
        SELECT p FROM Point p
        JOIN FETCH p.user
        WHERE p.user.id = :userId
          AND p.remainingAmount > 0
        ORDER BY p.expiresAt ASC, p.id ASC
    """)
    fun findAllByUserId(userId: Long): List<Point>

    /**
     * 사용자의 7일 이내 만료 예정 포인트 조회
     */
    @Query("""
        SELECT p FROM Point p
        JOIN FETCH p.user
        WHERE p.user.id = :userId
          AND p.status = 'ACTIVE'
          AND p.remainingAmount > 0
          AND p.expiresAt BETWEEN CURRENT_TIMESTAMP AND CURRENT_TIMESTAMP + 7 DAY
        ORDER BY p.expiresAt ASC
    """)
    fun findExpiringPointsIn7Days(userId: Long): List<Point>
}
