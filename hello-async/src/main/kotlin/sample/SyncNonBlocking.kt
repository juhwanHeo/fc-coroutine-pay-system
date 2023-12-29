package org.example.sample

import mu.KotlinLogging
import java.util.concurrent.CompletableFuture
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

fun main() {
    measureTimeMillis {
        val taskA = subA()
        subB()

        while (!taskA.isDone) {
            logger.debug { "...waiting A" }
            Thread.sleep(100)
        }
    }.let {
        logger.debug { ">> elapsed : $it ms" }
    }
}

private fun subA(): CompletableFuture<Unit> {
    return CompletableFuture.supplyAsync {
        logger.debug { "start A" }
        Thread.sleep(3000)
        logger.debug { "end A" }
    }
}

private fun subB() {
    logger.debug { "start B" }
    Thread.sleep(1000)
    logger.debug { "end B" }
}
