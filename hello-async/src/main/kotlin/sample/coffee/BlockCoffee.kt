package org.example.sample.coffee

import mu.KotlinLogging
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

fun main() {
    measureTimeMillis {
        repeat(2) {
            makeCoffee()
        }
    }.let {
        logger.debug { ">> elapsed: $it ms" }
    }
}

private fun makeCoffee() {
    grindCoffee()
    brewCoffee()
    boilMilk()
    formMilk()
    mixCoffeeAndMilk()
}
