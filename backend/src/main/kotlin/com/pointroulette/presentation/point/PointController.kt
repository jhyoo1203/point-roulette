package com.pointroulette.presentation.point

import com.pointroulette.application.point.PointService
import com.pointroulette.application.point.dto.PointBalanceResponse
import com.pointroulette.presentation.common.dto.ResponseData
import com.pointroulette.presentation.point.swagger.PointControllerDocs
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 포인트 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/points")
class PointController(
    private val pointService: PointService
) : PointControllerDocs {

    /**
     * 내 포인트 조회
     */
    @GetMapping("/{userId}")
    override fun getPointBalance(@PathVariable userId: Long): ResponseEntity<ResponseData<PointBalanceResponse>> {
        val response = pointService.getPointBalance(userId)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }
}
