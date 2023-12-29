package org.example.sample.coffee

import mu.KotlinLogging
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

private val employee = Schedulers.newSingle("employee")

fun main() {
    measureTimeMillis {
        Flux.range(1, 10).flatMap {
            makeCoffee()
        }.subscribeOn(employee)
            .blockLast()
//        makeCoffee()
//            .subscribeOn(employee)
//            .block()
    }.let {
        logger.debug { ">> elapsed: $it ms" }
    }
}

private fun makeCoffee(): Mono<Unit> {
    return Mono.zip(
        asyncGrindCoffee().then(asyncBrewCoffee()),
        asyncGrindCoffee().then(asyncFormMilk()),
    ).then(asyncMixCoffeeAndMilk())
}

private fun asyncGrindCoffee(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "커피 갈기" }}
        .delayElement(Duration.ofSeconds(1))
        .publishOn(employee)
        .doOnNext { logger.debug { "> 커피 가루" }}
}

private fun asyncBrewCoffee(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "커피 내리기" }}
        .delayElement(Duration.ofSeconds(1))
        .publishOn(employee)
        .doOnNext { logger.debug { "> 커피 원액" }}
}

private fun asyncBoilMilk(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "우유 끓이기" }}
        .delayElement(Duration.ofSeconds(1))
        .publishOn(employee)
        .doOnNext { logger.debug { "> 데운 우유" }}
}

private fun asyncFormMilk(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "우유 거품 내기" }}
        .delayElement(Duration.ofSeconds(1))
        .publishOn(employee)
        .doOnNext { logger.debug { "> 거품 우유" }}
}


private fun asyncMixCoffeeAndMilk(): Mono<Unit> {
    return Mono.fromCallable { logger.debug { "커피와 우유 섞기" }}
        .delayElement(Duration.ofSeconds(1))
        .publishOn(employee)
        .doOnNext { logger.debug { "> 카페 라떼" }}
}
