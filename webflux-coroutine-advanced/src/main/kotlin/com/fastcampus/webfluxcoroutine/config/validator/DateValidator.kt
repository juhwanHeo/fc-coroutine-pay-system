package com.fastcampus.webfluxcoroutine.config.validator

import com.fastcampus.webfluxcoroutine.config.extension.toLocalDate
import com.fastcampus.webfluxcoroutine.config.extension.toStringByFormat
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [DateValidator::class])
annotation class DateString(
    val message: String = "not a valid date",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class DateValidator: ConstraintValidator<DateString, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        // 2023-09-01
        // 2023/09/01
        // --> yyyyMMdd
        val text = value?.filter { it.isDigit() } ?: return true
        val format = "yyyyMMdd"
        return runCatching {
            text.toLocalDate(format).let {
                if (text != it.toStringByFormat(format)) null else true
            }
        }.getOrNull() != null
    }
}