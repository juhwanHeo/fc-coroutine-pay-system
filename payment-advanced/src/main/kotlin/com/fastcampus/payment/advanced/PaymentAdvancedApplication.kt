package com.fastcampus.payment.advanced

import com.fastcampus.payment.advanced.service.PaymentService
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@EnableR2dbcAuditing
@SpringBootApplication
class PaymentAdvancedApplication(
	private val paymentService: PaymentService,
): ApplicationRunner {
	override fun run(args: ApplicationArguments?) {
		runBlocking {
			paymentService.recaptureOnBoot()
		}
	}
}

fun main(args: Array<String>) {
	runApplication<PaymentAdvancedApplication>(*args)
}
