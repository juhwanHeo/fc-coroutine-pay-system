package com.fastcampus.webfluxcoroutine.controller

import com.fastcampus.webfluxcoroutine.config.validator.DateString
import com.fastcampus.webfluxcoroutine.service.AdvancedService
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
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

    @PutMapping("/test/error")
    suspend fun error(
        @RequestBody @Valid reqErrorTest: ReqErrorTest,
    ) {
        logger.debug { "/test/error request $reqErrorTest" }
//        throw RuntimeException("runtime exception !!")
    }
}

data class ReqErrorTest (
    @field:NotEmpty
    @field:Size(min=3, max=10)
    val id: String?,

    @field:NotNull
    @field:Positive(message = "양수만 입력 가능")
    @field:Max(100)
    val age: Int?,

    @DateString
    val birthday: String?,
)
