package com.fastcampus.payment.advanced.config

import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeoutException
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger {  }

@Component
class Locker(
    redisTemplate: ReactiveRedisTemplate<Any, Any>,
) {

    private val localLock = ConcurrentHashMap<SimpleKey, Boolean>()

    private val ops = redisTemplate.opsForValue()

    suspend fun <T> lock(key: SimpleKey, work: suspend () -> T): T {
        if (!tryLock(key)) throw TimeoutException("fail to obtain lock ($key)")
        try {
            return work.invoke()
        } finally {
            unLock(key)
        }
    }

    // spin lock
    // 몇초 기다렸다가 다시 요청
    private suspend fun tryLock(key: SimpleKey): Boolean {
        val start = System.nanoTime()

        // redisson
        // ops.tryLock() // pubsub 기반
        while (
            !localLock.contains(key) &&
            !ops.setIfAbsent(key, true, 10.seconds.toJavaDuration()).awaitSingle()
        ) {
            logger.debug { "- spring lock : $key" }
            delay(100)
            val elapsed = (System.nanoTime() - start).nanoseconds
            if (elapsed >= 10.seconds) return false
        }

        localLock[key] = true
        return true
    }

    private suspend fun unLock(key: SimpleKey) {
        try {
            ops.delete(key).awaitSingle()
        } catch (e: Exception) {
            logger.warn(e.message, e)
        } finally {
            localLock.remove(key)
        }
    }
}