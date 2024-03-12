package com.fastcampus.webfluxcoroutine.config.extension

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun String.toLocalDate(
    format: String = "yyyyMMdd",
): LocalDate {
    return LocalDate.parse(this.filter { it.isDigit() }, DateTimeFormatter.ofPattern(format))
}

fun LocalDate.toStringByFormat(
    format: String
): String {
    return this.format(DateTimeFormatter.ofPattern(format))
}