package com.pointroulette.presentation.admin.roulette

import com.pointroulette.application.roulette.RouletteService
import com.pointroulette.application.roulette.dto.RouletteHistoryResponse
import com.pointroulette.application.roulette.dto.RouletteParticipationResponse
import com.pointroulette.application.roulette.dto.RouletteParticipationSearchRequest
import com.pointroulette.common.model.PaginationResponse
import com.pointroulette.presentation.admin.roulette.swagger.AdminRouletteControllerDocs
import com.pointroulette.presentation.common.dto.ResponseData
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/roulette")
class AdminRouletteController(
    private val rouletteService: RouletteService
) : AdminRouletteControllerDocs {

    @GetMapping("/participations")
    override fun getParticipations(
        @ParameterObject @Valid @ModelAttribute searchRequest: RouletteParticipationSearchRequest
    ): ResponseEntity<ResponseData<PaginationResponse<RouletteParticipationResponse>>> {
        val response = rouletteService.getParticipations(searchRequest)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }

    @PostMapping("/participations/{historyId}/cancel")
    override fun cancelParticipation(
        @PathVariable historyId: Long
    ): ResponseEntity<ResponseData<RouletteHistoryResponse>> {
        val response = rouletteService.cancelParticipation(historyId)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }
}
