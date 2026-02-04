package com.pointroulette.application.user.dto

import com.pointroulette.domain.user.User
import io.swagger.v3.oas.annotations.media.Schema

/**
 * 로그인 응답 DTO
 */
@Schema(description = "로그인 응답")
data class LoginResponse(
    @field:Schema(description = "사용자 ID", example = "1")
    val id: Long,

    @field:Schema(description = "사용자 닉네임", example = "포인트왕")
    val nickname: String,

    @field:Schema(description = "신규 사용자 여부 (true: 회원가입, false: 로그인)", example = "false")
    val isNewUser: Boolean
) {
    companion object {
        fun from(user: User, isNewUser: Boolean): LoginResponse {
            return LoginResponse(
                id = user.id,
                nickname = user.nickname,
                isNewUser = isNewUser
            )
        }
    }
}
