package com.pointroulette.domain.roulette

import com.pointroulette.presentation.exception.BusinessException
import com.pointroulette.presentation.exception.ErrorCode

open class AlreadyParticipatedException(message: String = "오늘 이미 룰렛에 참여했습니다.")
    : BusinessException(ErrorCode.ALREADY_ROULETTE_PARTICIPATED, message)
