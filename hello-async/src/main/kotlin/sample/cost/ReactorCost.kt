package org.example.sample.cost

import mu.KotlinLogging
import reactor.core.publisher.Flux
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

fun main() {
    val count = AtomicLong()
    measureTimeMillis {
        Flux.range(1, 10_000).doOnNext {
            Flux.range(1, 100_000).doOnNext {
                count.addAndGet(1)
            }.subscribe()
        }.blockLast()

    }.let {
        logger.debug { ">> $count elapsed: $it ms" }
    }
}
