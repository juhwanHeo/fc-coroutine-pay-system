package com.fastcampus.payment.config.extension

fun <T> T?.query(f: (T) -> String): String {
    return when {
        this == null -> ""
        this is String && this.isBlank() -> ""
        this is Collection<*> && this.isEmpty() -> ""
        this is Array<*> && this.isEmpty() -> ""
        else -> f.invoke(this)
    }
}