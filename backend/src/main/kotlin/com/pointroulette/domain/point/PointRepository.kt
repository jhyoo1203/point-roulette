package com.pointroulette.domain.point

import org.springframework.data.jpa.repository.JpaRepository

/**
 * 포인트 Repository
 */
interface PointRepository : JpaRepository<Point, Long>
