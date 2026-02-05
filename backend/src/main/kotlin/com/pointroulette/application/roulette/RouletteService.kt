package com.pointroulette.application.roulette

import com.pointroulette.application.budget.DailyBudgetService
import com.pointroulette.application.point.PointService
import com.pointroulette.application.roulette.dto.RouletteHistoryResponse
import com.pointroulette.application.roulette.dto.RouletteParticipateResponse
import com.pointroulette.application.roulette.dto.RouletteStatusResponse
import com.pointroulette.application.user.UserService
import com.pointroulette.domain.point.PointSourceType
import com.pointroulette.domain.roulette.AlreadyParticipatedException
import com.pointroulette.domain.roulette.RouletteHistory
import com.pointroulette.domain.roulette.RouletteHistoryRepository
import com.pointroulette.domain.roulette.RouletteStatus
import com.pointroulette.infrastructure.util.RandomPointGenerator
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * 룰렛 서비스
 */
@Service
class RouletteService(
    private val rouletteHistoryRepository: RouletteHistoryRepository,
    private val dailyBudgetService: DailyBudgetService,
    private val pointService: PointService,
    private val userService: UserService,
    private val randomPointGenerator: RandomPointGenerator
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 룰렛에 참여합니다.
     *
     * @param userId 사용자 ID
     * @return RouletteParticipateResponse
     * @throws AlreadyParticipatedException 오늘 이미 참여한 경우
     */
    @Transactional
    fun participateRoulette(userId: Long): RouletteParticipateResponse {
        try {
            val today = LocalDate.now()

            // 1. 중복 참여 체크
            if (rouletteHistoryRepository.existsByUserIdAndParticipatedDate(userId, today)) {
                throw AlreadyParticipatedException()
            }

            // 2. 사용자 존재 확인
            val user = userService.getUser(userId)

            // 3. 오늘의 DailyBudget 조회
            val budget = dailyBudgetService.getTodayBudget()

            // 4. 랜덤 포인트 생성 (100-1000)
            val randomPoint = randomPointGenerator.generate()

            // 5. 예산 차감 시도
            dailyBudgetService.deductBudget(budget, randomPoint)

            val history = RouletteHistory(user, today, randomPoint, budget, RouletteStatus.SUCCESS)
            val savedHistory = rouletteHistoryRepository.save(history)

            // 7. 포인트 적립 (sourceId = history.id)
            pointService.earnPoint(userId, randomPoint, PointSourceType.ROULETTE, savedHistory.id)
            log.info("룰렛 참여 성공: userId=$userId, wonAmount=$randomPoint, remainingBudget=${budget.remainingAmount}")

            return RouletteParticipateResponse(true, randomPoint, budget.remainingAmount)

        } catch (e: DataIntegrityViolationException) {
            // unique constraint 위반 = 동시 요청으로 인한 중복 참여
            throw AlreadyParticipatedException("동시 요청으로 인한 중복 참여가 감지되었습니다.(${e.message})")
        }
    }

    /**
     * 오늘의 룰렛 참여 상태를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return RouletteStatusResponse
     */
    @Transactional(readOnly = true)
    fun getTodayStatus(userId: Long): RouletteStatusResponse {
        val today = LocalDate.now()

        // 사용자 존재 확인
        userService.getUser(userId)

        // 오늘 참여 이력 조회
        val todayHistory = rouletteHistoryRepository.findByUserIdAndParticipatedDate(userId, today)

        // 오늘 남은 예산 조회
        val todayBudget = dailyBudgetService.getTodayBudget()
        val remainingBudget = todayBudget.remainingAmount

        return RouletteStatusResponse(
            hasParticipatedToday = todayHistory != null,
            todayRemainingBudget = remainingBudget,
            lastParticipation = todayHistory?.let { RouletteHistoryResponse.from(it) }
        )
    }

}
