package com.pointroulette.presentation.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.pointroulette.application.user.dto.LoginRequest
import com.pointroulette.domain.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * UserController 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DisplayName("UserController 통합 테스트")
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
    }

    @Nested
    @DisplayName("POST /api/v1/users/login")
    inner class LoginApiTest {

        @Nested
        @DisplayName("성공 케이스")
        inner class SuccessTest {

            @Test
            @DisplayName("신규 사용자 로그인 - 201 응답과 isNewUser=true를 반환한다")
            fun `should return 200 with isNewUser true when new user login`() {
                // Given
                val request = LoginRequest(nickname = "신규유저")

                // When & Then
                mockMvc.post("/api/v1/users/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.nickname") { value("신규유저") }
                    jsonPath("$.data.isNewUser") { value(true) }
                }

                // 데이터베이스에 사용자가 생성되었는지 확인
                assertThat(userRepository.count()).isEqualTo(1)
                val savedUser = userRepository.findByNickname("신규유저")
                assertThat(savedUser).isNotNull
                assertThat(savedUser?.nickname).isEqualTo("신규유저")
            }

            @Test
            @DisplayName("기존 사용자 로그인 - 200 응답과 isNewUser=false를 반환한다")
            fun `should return 200 with isNewUser false when existing user login`() {
                // Given
                val nickname = "기존유저"
                val request = LoginRequest(nickname = nickname)

                // 기존 사용자 생성
                mockMvc.post("/api/v1/users/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }

                // When & Then
                mockMvc.post("/api/v1/users/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.httpStatus") { value(200) }
                    jsonPath("$.data.nickname") { value(nickname) }
                    jsonPath("$.data.isNewUser") { value(false) }
                }

                // 데이터베이스에 사용자가 1명만 있는지 확인
                assertThat(userRepository.count()).isEqualTo(1)
            }
        }

        @Nested
        @DisplayName("실패 케이스 - 유효성 검증")
        inner class ValidationFailTest {

            @Test
            @DisplayName("닉네임이 비어있으면 400 에러를 반환한다")
            fun `should return 400 when nickname is blank`() {
                // Given
                val request = mapOf("nickname" to "")

                // When & Then
                mockMvc.post("/api/v1/users/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("닉네임이 2자 미만이면 400 에러를 반환한다")
            fun `should return 400 when nickname is too short`() {
                // Given
                val request = LoginRequest(nickname = "A")

                // When & Then
                mockMvc.post("/api/v1/users/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }

            @Test
            @DisplayName("닉네임이 20자 초과이면 400 에러를 반환한다")
            fun `should return 400 when nickname is too long`() {
                // Given
                val request = LoginRequest(nickname = "A".repeat(21))

                // When & Then
                mockMvc.post("/api/v1/users/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }
}
