package com.fastcampus.webfluxcoroutine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@EnableR2dbcAuditing
@SpringBootApplication
class WebfluxCoroutineAdvancedApplication

fun main(args: Array<String>) {
	runApplication<WebfluxCoroutineAdvancedApplication>(*args)
}
