package com.pointroulette.domain.roulette

import com.pointroulette.presentation.exception.BusinessException
import com.pointroulette.presentation.exception.ErrorCode

open class RouletteParticipationException(message: String)
    : BusinessException(ErrorCode.ROULETTE_PARTICIPATION_FAILED, message)
