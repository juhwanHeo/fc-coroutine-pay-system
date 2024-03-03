package com.fastcampus.webfluxcoroutine.controller

import com.fastcampus.webfluxcoroutine.service.AdvancedService
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger { }

@RestController
class AdvancedController(
    private val advancedService: AdvancedService,
) {

    @GetMapping("/test/mdc")
    suspend fun testTxid() {
        logger.debug { "start MDC Txid" }
        delay(100)
        advancedService.mdc()
        logger.debug { "end MDC Txid" }
    }

    @GetMapping("/test/mdc2")
    fun testAnother() {
        logger.debug { "test another" }
    }
}