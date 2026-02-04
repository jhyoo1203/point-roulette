package com.pointroulette.presentation.user.swagger

import com.pointroulette.application.user.dto.LoginRequest
import com.pointroulette.application.user.dto.LoginResponse
import com.pointroulette.presentation.common.dto.ResponseData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody

/**
 * User Controller Swagger 문서
 */
@Tag(name = "User", description = "UserController")
interface UserControllerDocs {

    @Operation(
        summary = "로그인 (닉네임 기반 Mocking)",
        description = """
            닉네임을 입력하여 로그인합니다.
            - 닉네임이 존재하면 해당 사용자로 로그인
            - 닉네임이 존재하지 않으면 신규 회원가입 후 로그인
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ResponseData::class),
                    examples = [
                        ExampleObject(
                            name = "기존 사용자 로그인",
                            value = """{
                              "success": true,
                              "data": {
                                "id": 1,
                                "nickname": "포인트왕",
                                "isNewUser": false
                              }
                            }"""
                        ),
                        ExampleObject(
                            name = "신규 사용자 회원가입",
                            value = """{
                              "success": true,
                              "data": {
                                "id": 2,
                                "nickname": "룰렛왕",
                                "isNewUser": true
                              }
                            }"""
                        )
                    ]
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (닉네임 형식 오류)",
                content = [Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            value = """{
                              "success": false,
                              "error": {
                                "code": "C001",
                                "message": "nickname: 닉네임은 2자 이상 20자 이하로 입력해주세요"
                              }
                            }"""
                        )
                    ]
                )]
            )
        ]
    )
    fun login(
        @RequestBody
        @SwaggerRequestBody (
            description = "로그인 요청",
            required = true,
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = LoginRequest::class),
                examples = [
                    ExampleObject(
                        name = "로그인 요청 예시",
                        value = """{
                          "nickname": "포인트왕"
                        }"""
                    )
                ]
            )]
        )
        request: LoginRequest
    ): ResponseEntity<ResponseData<LoginResponse>>
}
