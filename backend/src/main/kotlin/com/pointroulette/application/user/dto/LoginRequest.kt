package com.pointroulette.application.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 로그인 요청 DTO
 */
@Schema(description = "로그인 요청")
data class LoginRequest(
    @field:NotBlank(message = "닉네임은 필수입니다")
    @field:Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요")
    @Schema(
        description = "사용자 닉네임",
        example = "포인트왕",
        minLength = 2,
        maxLength = 20
    )
    val nickname: String
)
