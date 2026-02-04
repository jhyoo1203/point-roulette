package com.pointroulette.presentation.user

import com.pointroulette.application.user.UserService
import com.pointroulette.application.user.dto.LoginRequest
import com.pointroulette.application.user.dto.LoginResponse
import com.pointroulette.presentation.common.dto.ResponseData
import com.pointroulette.presentation.user.swagger.UserControllerDocs
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 사용자 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) : UserControllerDocs {

    /**
     * 로그인 (닉네임 기반 Mocking)
     */
    @PostMapping("/login")
    override fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ResponseData<LoginResponse>> {
        val response = userService.login(request)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }
}
