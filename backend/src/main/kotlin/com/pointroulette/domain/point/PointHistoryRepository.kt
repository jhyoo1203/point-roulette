package com.pointroulette.domain.point

import org.springframework.data.jpa.repository.JpaRepository

/**
 * 포인트 이력 Repository
 */
interface PointHistoryRepository : JpaRepository<PointHistory, Long>
