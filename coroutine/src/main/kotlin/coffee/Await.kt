package com.fastcampus.coffee

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {  }

suspend fun main() {
    coroutineScope {

        val taskA = async { downloadA() }
        val taskB = async { downloadB() }
        val taskC = async { downloadC() }

//        listOf(
//            launch { downloadA() },
//            launch { downloadB() },
//            launch { downloadC() },
//        ).joinAll()

//        coroutineScope {
//            launch { downloadA() }
//            launch { downloadB() }
//            launch { downloadC() }
//        }
        val result = taskA.await() + taskB.await() + taskC.await()
        logger.debug { ">> end : $result" }
    }

    logger.debug { ">> end finally" }
}

private suspend fun downloadA(): Int {
    repeat(1) {
        logger.debug { "download A" }
        delay(1.seconds)
    }
    return 1
}

private suspend fun downloadB(): Int {
    repeat(3) {
        logger.debug { "download B" }
        delay(1.seconds)
    }
    return 3
}

private suspend fun downloadC(): Int {
    repeat(5) {
        logger.debug { "download C" }
        delay(1.seconds)
    }
    return 5
}