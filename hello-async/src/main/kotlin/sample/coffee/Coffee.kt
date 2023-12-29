package org.example.sample.coffee

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun grindCoffee() {
    logger.debug { "커피 갈기" }
    Thread.sleep(1000)
    logger.debug { "> 커피 가루" }
}

fun brewCoffee() {
    logger.debug { "커피 내리기" }
    Thread.sleep(1000)
    logger.debug { "> 커피 원액" }
}

fun boilMilk() {
    logger.debug { "우유 끓이기" }
    Thread.sleep(1000)
    logger.debug { "> 데운 우유" }
}

fun formMilk() {
    logger.debug { "우유 거품 내기" }
    Thread.sleep(1000)
    logger.debug { "> 거품 우유" }
}


fun mixCoffeeAndMilk() {
    logger.debug { "커피와 우유 섞기" }
    Thread.sleep(1000)
    logger.debug { "> 카페 라떼" }
}

