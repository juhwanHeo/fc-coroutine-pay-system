package org.example

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    logger.debug { "Hello World!"  + 1234 }
}