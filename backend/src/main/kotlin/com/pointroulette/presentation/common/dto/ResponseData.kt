package com.pointroulette.presentation.common.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.pointroulette.presentation.exception.ErrorCode
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseData<T>(
    val timestamp: LocalDateTime,
    val httpStatus: Int,
    val data: T? = null,
    val errorCode: ErrorCode? = null
) {
    private constructor(
        httpStatus: Int,
        data: T? = null,
        errorCode: ErrorCode? = null
    ) : this(
        timestamp = LocalDateTime.now(),
        httpStatus = httpStatus,
        data = data,
        errorCode = errorCode
    )

    companion object {
        fun <T> of(
            httpStatus: HttpStatus,
            data: T? = null,
        ): ResponseData<T> {
            return ResponseData(
                httpStatus = httpStatus.value(),
                data = data,
            )
        }

        fun <T> error(errorCode: ErrorCode): ResponseData<T> {
            return ResponseData(
                httpStatus = errorCode.httpStatus.value(),
                data = null,
                errorCode = errorCode
            )
        }
    }
}
