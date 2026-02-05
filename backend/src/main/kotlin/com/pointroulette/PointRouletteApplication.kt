package com.pointroulette

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@EnableRetry
@SpringBootApplication
class PointRouletteApplication

fun main(args: Array<String>) {
	runApplication<PointRouletteApplication>(*args)
}
