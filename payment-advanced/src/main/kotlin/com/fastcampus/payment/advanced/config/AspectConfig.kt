package com.fastcampus.payment.advanced.config

import kotlinx.coroutines.slf4j.MDCContext
import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.KotlinDetector
import org.springframework.stereotype.Component
import kotlin.coroutines.Continuation

private val logger = KotlinLogging.logger { }

@Aspect
@Component
class AspectConfig {
    @Around("""
        @annotation(org.springframework.web.bind.annotation.RequestMapping) ||
        @annotation(org.springframework.web.bind.annotation.GetMapping)     ||
        @annotation(org.springframework.web.bind.annotation.PostMapping)    ||
        @annotation(org.springframework.web.bind.annotation.PutMapping)     ||
        @annotation(org.springframework.web.bind.annotation.DeleteMapping) 
    """)
    fun wrapController(jp: ProceedingJoinPoint): Any? {
        return if (jp.hasSuspendFunction) {
            logger.debug { ">>> in suspend function" }
            val continuation = jp.args.last() as Continuation<*>
            val newContext = continuation.context + MDCContext()
            val newContinuation = Continuation(newContext) { continuation.resumeWith(it) }
            val newArgs = jp.args.dropLast(1) + newContinuation
            jp.proceed(newArgs.toTypedArray())
        }
        else {
            logger.debug { ">>> in non suspend function" }
            jp.proceed()
        }
    }

    private val ProceedingJoinPoint.hasSuspendFunction: Boolean
        get() {
            val method = (this.signature as MethodSignature).method
            return KotlinDetector.isSuspendingFunction(method)
        }
}