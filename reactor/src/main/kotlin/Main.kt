package com.fastcampus

import mu.KotlinLogging
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {  }

fun main() {
    // 단건 처리
    Mono.just(9)
        .map { it + 1 }
        .doOnNext {
            logger.debug { "from publisher -> $it" }
        }
        .log()
        .subscribe()

    Flux.just(arrayOf(1, 2, 3, 4), arrayOf(5, 6, 7, 8))
        .map { it + 1 }
        .log()
        .subscribe()

    Flux.range(1, 10)
        .map { it * it } // block
        .log()
        .subscribe()

    Flux.range(1, 10)
        .flatMap { Mono.just(it * it) } // non-block
        .log()
        .subscribe()


    Mono.just(1)
        .flux()
        .log()
        .subscribe()
}