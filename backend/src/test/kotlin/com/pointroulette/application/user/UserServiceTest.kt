package com.pointroulette.application.user

import com.pointroulette.application.user.dto.LoginRequest
import com.pointroulette.domain.user.User
import com.pointroulette.domain.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.never
import org.mockito.BDDMockito.verify
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

/**
 * UserService 단위 테스트
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    @Nested
    @DisplayName("login 메서드")
    inner class LoginTest {

        @Nested
        @DisplayName("신규 사용자인 경우")
        inner class NewUserTest {

            @Test
            @DisplayName("사용자를 생성하고 isNewUser가 true인 응답을 반환한다")
            fun `should create new user and return response with isNewUser true`() {
                // Given
                val nickname = "신규유저"
                val request = LoginRequest(nickname = nickname)
                val savedUser = User(nickname = nickname)

                given(userRepository.findByNickname(nickname)).willReturn(null)
                given(userRepository.save(any())).willReturn(savedUser)

                // When
                val response = userService.login(request)

                // Then
                assertThat(response.nickname).isEqualTo(nickname)
                assertThat(response.isNewUser).isTrue()
                verify(userRepository).findByNickname(nickname)
                verify(userRepository).save(any())
            }
        }

        @Nested
        @DisplayName("기존 사용자인 경우")
        inner class ExistingUserTest {

            @Test
            @DisplayName("기존 사용자를 반환하고 isNewUser가 false인 응답을 반환한다")
            fun `should return existing user and response with isNewUser false`() {
                // Given
                val nickname = "기존유저"
                val request = LoginRequest(nickname = nickname)
                val existingUser = User(nickname = nickname)

                given(userRepository.findByNickname(nickname)).willReturn(existingUser)

                // When
                val response = userService.login(request)

                // Then
                assertThat(response.nickname).isEqualTo(nickname)
                assertThat(response.isNewUser).isFalse()
                verify(userRepository).findByNickname(nickname)
                verify(userRepository, never()).save(any())
            }
        }
    }
}
