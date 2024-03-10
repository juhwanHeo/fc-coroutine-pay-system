package com.fastcampus.webfluxcoroutine.config

import com.fastcampus.webfluxcoroutine.config.extension.txid
import io.micrometer.context.ContextRegistry
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.util.*

const val KEY_TXID = "txid"

private val logger = KotlinLogging.logger {}

@Component
@Order(1)
class MdcFilter: WebFilter {
    init {
        propagateMdcThroughReactor()
    }

    /*
    * Reactor 기반 라이브러리 호출 시에도
    * MDC 가 이어질 수 있도록 설정
    * */
    private fun propagateMdcThroughReactor() {
        Hooks.enableAutomaticContextPropagation()
        ContextRegistry.getInstance()
            .registerThreadLocalAccessor(
                KEY_TXID,
                { MDC.get(KEY_TXID) },
                { value -> MDC.put(KEY_TXID, value) },
                { MDC.remove(KEY_TXID) }
            )
    }

    /*
    * Reactor 기반 라이브러리 호출 시에도
    * MDC 가 이어질 수 있도록 설정
    * */
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val uuid =  exchange.request.headers["x-txid"]?.firstOrNull() ?: "${UUID.randomUUID()}"
        MDC.put(KEY_TXID, uuid)
        return chain.filter(exchange).contextWrite {
            Context.of(KEY_TXID, uuid)
        }.doOnError {
            exchange.request.txid = uuid
        }
    }
}
