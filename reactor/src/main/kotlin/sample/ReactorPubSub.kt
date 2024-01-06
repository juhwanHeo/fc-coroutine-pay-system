package com.fastcampus.sample

import mu.KotlinLogging
import reactor.core.publisher.Flux
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.time.Duration

private val logger = KotlinLogging.logger {  }


private val scheduler = Schedulers.newSingle("worker")

fun main() {
    Flux.range(1, 12)
        .doOnNext { logger.debug { "1st: $it" } }
        .filter { it % 2 == 0}
        .doOnNext { logger.debug { "2st: $it" } }
        .filter { it % 3 == 0}
        .publishOn(scheduler) // nio 같은 오래 걸리는 작업이 다음 체인에 있을 경우 사용
        .delayElements(Duration.ofMillis(10))
        .publishOn(scheduler)
        .doOnNext { logger.debug { "3st: $it" } }
        .filter { it % 4 == 0}
        .doOnNext { logger.debug { "4st: $it" } }
        .subscribeOn(scheduler) // 위 체인이 느리고, 빠져나가서 다음 체인을 빠르게 처리해야할 경우 사용
        .blockLast()
}