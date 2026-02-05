package com.pointroulette.infrastructure.util

import org.springframework.stereotype.Component
import java.security.SecureRandom

/**
 * SecureRandom을 사용한 랜덤 포인트 생성기
 */
@Component
class RouletteRandomUtil : RandomPointGenerator {

    companion object {
        private const val MIN_POINT = 100
        private const val MAX_POINT = 1000
    }

    private val random = SecureRandom()

    /**
     * 100-1000 사이의 랜덤 포인트를 생성합니다.
     *
     * @return 랜덤 포인트 (100~1000)
     */
    override fun generate(): Int {
        return MIN_POINT + random.nextInt(MAX_POINT - MIN_POINT + 1)
    }
}
