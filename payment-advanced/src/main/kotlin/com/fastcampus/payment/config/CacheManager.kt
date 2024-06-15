package com.fastcampus.payment.config

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.KotlinLogging
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import kotlin.time.Duration
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger {}

@Component
class CacheManager(
    redisTemplate: ReactiveRedisTemplate<Any, Any>,
) {
    private val ops = redisTemplate.opsForValue()

    val ttl = HashMap<Any, Duration>()

    suspend fun <T> get(key: CacheKey): T? {
        return ops.get(key).awaitSingleOrNull()?.let { it as T }.also { logger.debug { "cache read by $key: $it" } }
    }

    suspend fun <T> get(key: CacheKey, supplier: suspend () -> T?): T? {
        return get(key) ?: supplier.invoke()?.also { set(key, it) }
    }

    suspend fun set(key: CacheKey, value: Any) {
        val ttl = ttl[key.group]?.toJavaDuration()

        if (ttl == null) {
            ops.set(key, value)
        }
        else {
            ops.set(key, value, ttl)
        }.awaitSingle()
    }

    suspend fun delete(key: CacheKey) {
        ops.delete(key).awaitSingle()
    }
}


class CacheKey(val group: Any, vararg elements: Any): SimpleKey(group, *elements)