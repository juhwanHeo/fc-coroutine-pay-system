package com.fastcampus.payment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@EnableR2dbcAuditing
@SpringBootApplication
class PaymentAdvancedApplication

fun main(args: Array<String>) {
	runApplication<PaymentAdvancedApplication>(*args)
}
