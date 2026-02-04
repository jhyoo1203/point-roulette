package com.pointroulette.infrastructure.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Swagger/OpenAPI 설정
 */
@Configuration
class SwaggerConfig(
    @Value("\${swagger.server.local.url}") private val localUrl: String,
//    @Value("\${swagger.server.production.url}") private val productionUrl: String
) {

    @Bean
    fun openAPI(): OpenAPI {
        val info = Info()
            .title("Point Roulette API")
            .version("1.0.0")
            .description("포인트 룰렛 시스템 API 문서")

        val localServer = Server()
            .url(localUrl)
            .description("Local server")

//        val productionServer = Server()
//            .url(productionUrl)
//            .description("Production server(실제로는 production에 swagger를 노출하지 않지만 과제 제출을 위해 설정)")

        return OpenAPI()
            .info(info)
            .servers(listOf(localServer))
    }

    @Bean
    fun userApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("user-api")
            .pathsToMatch("/api/v1/users/**")
            .build()
    }
}
