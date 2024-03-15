package com.fastcampus.webfluxcoroutine.example

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.ActiveProfiles
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@SpringBootTest
@ActiveProfiles("test")
class RedisTemplateTest(
    private val redisTemplate: ReactiveRedisTemplate<Any, Any>,
): StringSpec({
    val KEY = "key"

    afterEach {
        redisTemplate.delete(KEY).awaitSingle()
    }

    "hello reactive redis" {
        val ops = redisTemplate.opsForValue()

        shouldThrow<NoSuchElementException> {
            ops.get(KEY).awaitSingle()
        }

        ops.set(KEY, "blabla fastcampus").awaitSingle()
        ops.get(KEY).awaitSingle() shouldBe "blabla fastcampus"

        redisTemplate.expire(KEY, 3.seconds.toJavaDuration()).awaitSingle()
        delay(5.seconds)

        shouldThrow<NoSuchElementException> {
            ops.get(KEY).awaitSingle()
        }
    }
})
