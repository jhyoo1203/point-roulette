package com.pointroulette.presentation.roulette

import com.pointroulette.application.roulette.RouletteService
import com.pointroulette.application.roulette.dto.RouletteParticipateResponse
import com.pointroulette.application.roulette.dto.RouletteStatusResponse
import com.pointroulette.presentation.common.dto.ResponseData
import com.pointroulette.presentation.roulette.swagger.RouletteControllerDocs
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/roulette")
class RouletteController(
    private val rouletteService: RouletteService
) : RouletteControllerDocs {

    @PostMapping("/participate/{userId}")
    override fun participate(@PathVariable userId: Long): ResponseEntity<ResponseData<RouletteParticipateResponse>> {
        val response = rouletteService.participateRoulette(userId)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }

    @GetMapping("/status/{userId}")
    override fun getStatus(@PathVariable userId: Long): ResponseEntity<ResponseData<RouletteStatusResponse>> {
        val response = rouletteService.getTodayStatus(userId)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }
}
