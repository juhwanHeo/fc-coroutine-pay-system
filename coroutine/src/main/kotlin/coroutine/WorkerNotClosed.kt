package com.fastcampus.coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    runBlocking {
        launch {
            workHard()
        }
        launch {
            workEasy()
        }
    }
}

private suspend fun workEasy() {
    logger.debug { "start easy work" }
    delay(1_000)
    logger.debug { "end easy work" }
}

private suspend fun workHard() {
    logger.debug { "start hard work" }
    while (true) { }
    logger.debug { "end hard work" }
}

