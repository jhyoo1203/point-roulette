package com.pointroulette.domain.user

import com.pointroulette.domain.common.BaseEntity
import jakarta.persistence.*

/**
 * 사용자 엔티티
 */
@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "uk_users_nickname", columnList = "nickname", unique = true)
    ]
)
class User(
    @Column(nullable = false, unique = true)
    var nickname: String
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
