package com.pointroulette.application.user

import com.pointroulette.application.user.dto.LoginRequest
import com.pointroulette.application.user.dto.LoginResponse
import com.pointroulette.domain.user.User
import com.pointroulette.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 사용자 서비스
 */
@Service
class UserService(
    private val userRepository: UserRepository,
) {

    /**
     * 로그인 (닉네임 기반 Mocking)
     * - 닉네임이 존재하면 로그인
     * - 닉네임이 존재하지 않으면 회원가입
     */
    @Transactional
    fun login(request: LoginRequest): LoginResponse {
        val existingUser = userRepository.findByNickname(request.nickname)

        if (existingUser == null) {
            val newUser = userRepository.save(User(request.nickname))
            return LoginResponse.from(newUser, isNewUser = true)
        }

        return LoginResponse.from(existingUser, isNewUser = false)
    }
}
