package com.pointroulette.infrastructure.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import java.util.TimeZone

@Configuration
class TimeZoneConfig {

	@PostConstruct
	fun init() {
		// JVM 전역 시간대를 한국 시간(Asia/Seoul)로 설정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
	}
}
