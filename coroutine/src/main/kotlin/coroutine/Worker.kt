package com.fastcampus.coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

//
// thread 에 workEasy, workHard 요청 >> queue에 적재 >> event loop에 의해 poll >> poll한 task 실행

fun main() {
    runBlocking {
        val taskHard = launch {
            workHard()
        }
        val taskEasy = launch {
            workEasy()
        }

        delay(3_000)
        taskHard.cancel()
        logger.debug { "end !!" }
    }
}

private suspend fun workEasy() {
    logger.debug { "start easy work" }
    delay(1_000)
    logger.debug { "end easy work" }
}

private suspend fun workHard() {
    logger.debug { "start hard work" }
//    delay(3_000)

    try {
        while (true) {
            delay(100)
        }
    } finally {
        logger.debug { "end hard work" }
    }
}

