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
    @param:Value("\${swagger.server.local.url}") private val localUrl: String,
    @param:Value("\${swagger.server.production.url}") private val productionUrl: String
) {

    @Bean
    fun openAPI(): OpenAPI {
        val info = Info()
            .title("Point Roulette API")
            .version("1.0.0")
            .description("포인트 룰렛 시스템 API 문서")

        val productionServer = Server()
            .url(productionUrl)
            .description("Production server(실제로는 production에 swagger를 노출하지 않지만 과제 제출을 위해 설정)")

        val localServer = Server()
            .url(localUrl)
            .description("Local server")

        return OpenAPI()
            .info(info)
            .servers(listOf(productionServer, localServer))
    }

    @Bean
    fun userApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("user-api")
            .pathsToMatch("/api/v1/users/**")
            .build()
    }

    @Bean
    fun adminProductApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("admin-product-api")
            .pathsToMatch("/api/v1/admin/products/**")
            .build()
    }

    @Bean
    fun adminBudgetApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("admin-budget-api")
            .pathsToMatch("/api/v1/admin/budgets/**")
            .build()
    }

    @Bean
    fun rouletteApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("roulette-api")
            .pathsToMatch("/api/v1/roulette/**")
            .build()
    }

    @Bean
    fun productApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("product-api")
            .pathsToMatch("/api/v1/products/**")
            .build()
    }

    @Bean
    fun adminRouletteApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("admin-roulette-api")
            .pathsToMatch("/api/v1/admin/roulette/**")
            .build()
    }

    @Bean
    fun pointApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("point-api")
            .pathsToMatch("/api/v1/points/**")
            .build()
    }
}
