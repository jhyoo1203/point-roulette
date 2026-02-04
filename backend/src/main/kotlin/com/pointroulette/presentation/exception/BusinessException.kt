package com.pointroulette.presentation.exception

class BusinessException (
    val errorCode: ErrorCode
) : RuntimeException()
