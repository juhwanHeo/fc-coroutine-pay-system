package com.fastcampus.coffee

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {  }

private val worker = newSingleThreadContext("employee")

fun main() {
    measureTimeMillis {
        runBlocking {
            repeat(10) {
                launch(worker) {
                    makeCoffee()
                }
            }
        }
    }.let { logger.debug { ">> elapsed: $it ms" } }

    // block 함수와 같은 기능
    // Mono.just(1).block()
}

private suspend fun makeCoffee() {
    coroutineScope {
        launch {
            grindCoffee()
            brewCoffee()

        }
        launch {
            boilMilk()
            formMilk()
        }
    }
    // launch 가 다 끝난 다음에 실행됨
    mixCoffeeAndMilk()
}

private suspend fun grindCoffee() {
    logger.debug { "커피 갈기" }
    delay(1_000)
    logger.debug { "> 커피 가루" }
}

private suspend fun brewCoffee() {
    logger.debug { "커피 내리기" }
    delay(1_000)
    logger.debug { "> 커피 원액" }
}

private suspend fun boilMilk() {
    logger.debug { "우유 끓이기" }
    delay(1_000)
    logger.debug { "> 데운 우유" }
}

private suspend fun formMilk() {
    logger.debug { "우유 거품 내기" }
    delay(1_000)
    logger.debug { "> 거품 우유" }
}


private suspend fun mixCoffeeAndMilk() {
    logger.debug { "커피와 우유 섞기" }
    delay(1_000)
    logger.debug { "> 카페 라떼" }
}

