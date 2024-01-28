package com.fastcampus.coffee

import kotlinx.coroutines.delay
import mu.KotlinLogging

private val logger = KotlinLogging.logger {  }

suspend fun main() {
    logger.debug { "start" }
    delay(1_000)
    logger.debug { "end" }
}