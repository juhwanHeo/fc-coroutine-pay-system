package com.fastcampus.sample

import mu.KotlinLogging
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {  }

fun getRequest(): Mono<Int> {
    return Mono.just(1)
}

fun subA(request: Mono<Int>): Mono<Int> {
    return request.map { it + 1 }
}

// param 일반 타입
// return reactor 타입
// 이런 방식님 좀 더 깔끔하게 코딩 가능
fun subA(request: Int): Mono<Int> {
    return Mono.fromCallable { request + 1 }
}

fun subB(mono: Mono<Int>): Mono<Int> {
    return mono.map { it + 2 }
}

fun subB(param: Int): Mono<Int> {
    return Mono.fromCallable { param + 2 }
}

fun main() {
    block()
    nonblock()
    nonblockChain()
}

private fun block() {
    val request = getRequest()

    logger.debug { ">> request: ${request.block()}" }

    val resA = subA(request)

    logger.debug { ">> resA: ${resA.block()}" }

    val resB = subB(resA)

    logger.debug { ">> resB: ${resB.block()}" }
}

private fun nonblock() {
    val request = getRequest().doOnNext { logger.debug { ">> request: $it" } }
    val resA = subA(request).doOnNext { logger.debug { ">> resA: $it" } }
    val resB = subB(resA).doOnNext { logger.debug { ">> resB: $it" } }

    resB.subscribe()
}

private fun nonblockChain() {
    getRequest()
        .doOnNext { logger.debug { ">> request: $it" } }
//        .flatMap { subA(Mono.just(it)) }
        .flatMap { subA(it) }
        .doOnNext { logger.debug { ">> subA: $it" }}
//        .flatMap { subB(Mono.just(it)) }
        .flatMap { subB(it) }
        .doOnNext { logger.debug { ">> subB: $it" } }
        .subscribe()
}
