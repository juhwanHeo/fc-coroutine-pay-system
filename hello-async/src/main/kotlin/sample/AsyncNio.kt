package org.example.sample

import mu.KotlinLogging
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

val single = Schedulers.newSingle("worker")

fun main() {
    measureTimeMillis {
        Mono.zip(
            subA(),
            subB(),
        ).subscribeOn(single).block()
    }.let {
        logger.debug { ">> elapsed: $it ms" }
    }
}

private fun subA(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "start A" } }
        .delayElement(Duration.ofSeconds(1))
        .publishOn(single)
        .doOnNext { logger.debug { "end A" } }
}

private fun subB(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "start B" } }
        .delayElement(Duration.ofSeconds(1))
        .publishOn(single)
        .doOnNext { logger.debug { "end B" } }
}
