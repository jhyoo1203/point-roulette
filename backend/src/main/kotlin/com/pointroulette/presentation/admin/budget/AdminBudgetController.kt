package com.pointroulette.presentation.admin.budget

import com.pointroulette.application.budget.DailyBudgetService
import com.pointroulette.application.budget.dto.DailyBudgetCreateRequest
import com.pointroulette.application.budget.dto.DailyBudgetResponse
import com.pointroulette.application.budget.dto.DailyBudgetSearchRequest
import com.pointroulette.common.model.PaginationResponse
import com.pointroulette.presentation.admin.budget.swagger.AdminBudgetControllerDocs
import com.pointroulette.presentation.common.dto.ResponseData
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/budgets")
class AdminBudgetController(
    private val dailyBudgetService: DailyBudgetService
) : AdminBudgetControllerDocs {

    @PostMapping
    override fun createDailyBudgets(
        @Valid @RequestBody request: DailyBudgetCreateRequest
    ): ResponseEntity<ResponseData<List<DailyBudgetResponse>>> {
        val response = dailyBudgetService.createDailyBudgets(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.of(HttpStatus.CREATED, response))
    }

    @GetMapping
    override fun getDailyBudgets(
        @ParameterObject @Valid @ModelAttribute searchRequest: DailyBudgetSearchRequest
    ): ResponseEntity<ResponseData<PaginationResponse<DailyBudgetResponse>>> {
        val response = dailyBudgetService.getDailyBudgets(searchRequest)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }
}
