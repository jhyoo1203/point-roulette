package com.pointroulette.presentation.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.pointroulette.infrastructure.util.logger
import com.pointroulette.presentation.common.dto.ResponseData
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import kotlin.getValue

/**
 * 전역 예외 처리 핸들러
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log by logger()

    /**
     * BusinessException 처리
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        ex: BusinessException
    ): ResponseEntity<ResponseData<Nothing>> {
        val response = ResponseData.error<Nothing>(ex.errorCode)
        val caller = getCallerInfo(ex)
        log.warn("$caller - Business exception: ${ex.errorCode.message}")
        return ResponseEntity.status(ex.errorCode.httpStatus).body(response)
    }

    /**
     * ResourceNotFoundException 처리
     */
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException
    ): ResponseEntity<ResponseData<Nothing>> {
        val response = ResponseData.error<Nothing>(ErrorCode.RESOURCE_NOT_FOUND)
        val caller = getCallerInfo(ex)
        log.warn("$caller - Resource not found: ${ex.message}")
        return ResponseEntity.status(ErrorCode.RESOURCE_NOT_FOUND.httpStatus).body(response)
    }

    /**
     * Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<ResponseData<Nothing>> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "유효하지 않은 값입니다")
        }

        val response = ResponseData.error<Nothing>(ErrorCode.INVALID_PARAMETER)
        val caller = getCallerInfo(ex)
        log.warn("$caller - Validation failed: $errors")
        return ResponseEntity.status(ErrorCode.INVALID_PARAMETER.httpStatus).body(response)
    }



    /**
     * JSON 파싱 오류 처리 (Enum 값 불일치 등)
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException
    ): ResponseEntity<ResponseData<Nothing>> {
        val cause = ex.cause

        // Enum 타입 불일치 오류 처리
        if (cause is InvalidFormatException) {
            val targetType = cause.targetType
            if (targetType != null && targetType.isEnum) {
                val allowedValues = targetType.enumConstants.joinToString(", ")
                val invalidValue = cause.value
                val errorMessage = "입력된 값 '${invalidValue}'은(는) 유효하지 않습니다. 허용된 값: [$allowedValues]"

                log.warn("Enum validation failed: $errorMessage")

                return ResponseEntity.status(ErrorCode.INVALID_PARAMETER.httpStatus).body(
                    ResponseData.error(ErrorCode.INVALID_PARAMETER)
                )
            }
        }

        val response = ResponseData.error<Nothing>(ErrorCode.INVALID_PARAMETER)
        log.warn("Message not readable: ${ex.message}")
        return ResponseEntity.status(ErrorCode.INVALID_PARAMETER.httpStatus).body(response)
    }

    /**
     * IllegalStateException 처리 (비즈니스 로직 상태 오류)
     */
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(
        ex: IllegalStateException
    ): ResponseEntity<ResponseData<Nothing>> {
        val response = ResponseData.error<Nothing>(ErrorCode.BUSINESS_ERROR)
        val caller = getCallerInfo(ex)
        log.warn("$caller - Illegal state: ${ex.message}")
        return ResponseEntity.status(ErrorCode.BUSINESS_ERROR.httpStatus).body(response)
    }

    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleException(
        ex: Exception
    ): ResponseEntity<ResponseData<Nothing>> {
        val response = ResponseData.error<Nothing>(ErrorCode.INTERNAL_SERVER_ERROR)
        log.error("Unhandled exception occurred", ex)
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.httpStatus).body(response)
    }

    /**
     * 예외가 발생한 클래스와 메서드 정보 추출
     */
    private fun getCallerInfo(ex: Exception): String {
        val stackTrace = ex.stackTrace
        if (stackTrace.isNotEmpty()) {
            val element = stackTrace.first()
            val className = element.className.substringAfterLast('.')
            val methodName = element.methodName
            return "$className.$methodName"
        }
        return "Unknown"
    }
}
