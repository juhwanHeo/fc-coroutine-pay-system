package com.fastcampus.webfluxcoroutine.config

import com.fastcampus.webfluxcoroutine.config.extension.txid
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerRequest

private val logger = KotlinLogging.logger {}

@Configuration
class ErrorConfig {

    @Bean
    fun errorAttribute(): DefaultErrorAttributes {
        return object: DefaultErrorAttributes() {
            override fun getErrorAttributes(
                serverRequest: ServerRequest,
                options: ErrorAttributeOptions,
            ): MutableMap<String, Any> {
                val request = serverRequest.exchange().request
                val txid = request.txid ?: ""
                MDC.put(KEY_TXID, txid)
                try {
                    super.getError(serverRequest).let { e ->
                        logger.error(e.message ?: "Internal Server Error", e)
                    }
                    return super.getErrorAttributes(serverRequest, options).apply {
                        remove("requestId")
                        put(KEY_TXID, txid)
                    }
                } finally {
                    request.txid = null
                    MDC.remove(KEY_TXID)
                }
            }
        }
    }
}