package org.example.sample.coffee

import mu.KotlinLogging
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

private val employees = Executors.newFixedThreadPool(3)

fun main() {
    measureTimeMillis {
        repeat(10) {
            makeCoffee()
        }
        employees.shutdown()
        employees.awaitTermination(100, TimeUnit.SECONDS)
    }.let {
        logger.debug { ">> elapsed: $it ms" }
    }
}

private fun makeCoffee() {
    val taskA = employees.submit {
        grindCoffee()
        brewCoffee()
    }
    val taskB = employees.submit {
        boilMilk()
        formMilk()
    }

    employees.submit {
        while ( !taskA.isDone || !taskB.isDone ) {
            mixCoffeeAndMilk()
        }
    }
}
