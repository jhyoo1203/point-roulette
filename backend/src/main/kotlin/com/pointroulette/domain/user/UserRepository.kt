package com.pointroulette.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * 사용자 Repository
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {

    /**
     * 닉네임으로 사용자 조회
     */
    fun findByNickname(nickname: String): User?
}
