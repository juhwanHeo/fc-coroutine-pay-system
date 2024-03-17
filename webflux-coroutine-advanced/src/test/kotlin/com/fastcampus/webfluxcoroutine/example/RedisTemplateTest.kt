package com.fastcampus.webfluxcoroutine.example

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Range
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.redis.connection.DataType
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveZSetOperations
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import java.util.Date
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger {  }

@SpringBootTest
@ActiveProfiles("test")
class RedisTemplateTest(
    private val redisTemplate: ReactiveRedisTemplate<Any, Any>,
): WithRedisContainer, StringSpec({
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

    /*
    * 스택, 큐
    * 대기열 관리, LRU Cache
    * */
    "LinkedList" {
        val ops = redisTemplate.opsForList()
        ops.rightPushAll(KEY, 2, 3, 4, 5).awaitSingle()

        redisTemplate.type(KEY).awaitSingle() shouldBe DataType.LIST
        ops.size(KEY).awaitSingle() shouldBe 4

//        for (i in 0..< ops.size(KEY).awaitSingle()) {
//            ops.index(KEY, i).awaitSingle().let {
//                logger.debug { "$i: $it" }
//            }
//        }

//        ops.range(KEY, 0, -1).asFlow().collect { logger.debug { "$it" } }
//        ops.range(KEY, 0, -1).toStream().forEach { logger.debug { "$it" } }
        ops.all(KEY) shouldBe listOf(2, 3, 4, 5)

        ops.rightPush(KEY, 6).awaitSingle()
        ops.all(KEY) shouldBe listOf(2, 3, 4, 5, 6)

        ops.leftPop(KEY).awaitSingle() shouldBe 2
        ops.all(KEY) shouldBe listOf(3, 4, 5, 6)


        ops.leftPush(KEY, 9).awaitSingle()
        ops.all(KEY) shouldBe listOf(9, 3, 4, 5, 6)
        ops.rightPop(KEY).awaitSingle() shouldBe 6
        ops.all(KEY) shouldBe listOf(9, 3, 4, 5)
    }

    "LinkedList LRU" {
        val ops = redisTemplate.opsForList()
        ops.rightPushAll(KEY, 7, 6, 4, 3, 2, 1, 3).awaitSingle()

        ops.remove(KEY, 0, 2).awaitSingle()
        ops.all(KEY) shouldBe listOf(7, 6, 4, 3, 1, 3)

        ops.leftPush(KEY, 2).awaitSingle()
        ops.all(KEY) shouldBe listOf(2, 7, 6, 4, 3, 1, 3)
    }

    "HashMap" {
        val ops = redisTemplate.opsForHash<Int, String>()
        val map = (1..10).map { it to "val-$it" }.toMap()

        ops.putAll(KEY, map).awaitSingle()

        ops.size(KEY).awaitSingle() shouldBe 10
        ops.get(KEY, 1).awaitSingle() shouldBe "val-1"
        ops.get(KEY, 8).awaitSingle() shouldBe "val-8"
    }

    /*
    * LRU 가능
    * */
    "sorted set" {
        val ops = redisTemplate.opsForZSet()
        listOf(8, 7, 1, 4, 13, 22, 9, 7, 8).forEach {
            ops.add(KEY, "$it", - 1.0 * Date().time).awaitSingle()
//            ops.all(KEY).let { logger.debug { it } }
        }

        redisTemplate.delete(KEY).awaitSingle()

        listOf(
            "jake"      to 123,
            "chulsoo"   to 753,
            "yeonghee"  to 932,
            "john"      to 335,
            "jake"      to 623,
        ).also {
            it.toMap().toList().sortedBy { it.second }.let { logger.debug { "original: $it" } }
        }.forEach {
            ops.add(KEY, it.first, it.second * -1.0).awaitSingle()
            ops.all(KEY).let { logger.debug { it } }
        }
    }

    "geo redis" {
        val ops = redisTemplate.opsForGeo()

        listOf(
            GeoLocation("seoul",   Point(126.97806, 37.56667)),
            GeoLocation("busan",   Point(129.07556, 35.17944)),
            GeoLocation("incheon", Point(126.70528, 37.45639)),
            GeoLocation("daegu",   Point(128.60250, 35.87222)),
            GeoLocation("anyang",  Point(126.95556, 37.39444)),
            GeoLocation("daejeon", Point(127.38500, 36.35111)),
            GeoLocation("gwangju", Point(126.85306, 35.15972)),
            GeoLocation("suwon",   Point(127.02861, 37.26389)),
        ).forEach {
            ops.add(KEY,it as GeoLocation<Any>).awaitSingle()
        }

        ops.distance(KEY, "seoul", "busan").awaitSingle().let { logger.debug { "seoul -> busan : $it"} }

        val p = ops.position(KEY, "daegu").awaitSingle().also { logger.debug { it } }


        val circle = Circle(p, Distance(200.0, Metrics.KILOMETERS))

        ops.radius(KEY, circle).asFlow().map { it.content.name }.toList().let {
            logger.debug { "cities near daegu: $it" }
        }
    }

    "hyper loglog" {
        val ops = redisTemplate.opsForHyperLogLog()
        ops.add("page1","192.179.0.23","41.61.2.230","225.105.161.131").awaitSingle()
        ops.add("page2","1.1.1.1","2.2.2.2").awaitSingle()
        ops.add("page3","9.9.9.9").awaitSingle()
        ops.add("page3","8.8.8.8").awaitSingle()
        ops.add("page3","7.7.7.7","2.2.2.2","1.1.1.1").awaitSingle()
        ops.size("page3").awaitSingle().let { logger.debug { it } }
    }

    "pub / sub" {
        val channel1 = "channel-1"
        redisTemplate.listenToChannel(channel1).doOnNext {
            logger.debug { ">> received 1: ${it.message}" }
        }.subscribe()

        redisTemplate.listenToChannel(channel1).doOnNext {
            logger.debug { ">> received 2: ${it.message}" }
        }.subscribe()

        redisTemplate.listenToChannel(channel1).asFlow().onEach {
            logger.debug { ">> received 3: ${it.message}" }
        }.launchIn(CoroutineScope(Dispatchers.Default))

        repeat(10) {
            val message = "Test Message (${it + 1})"
            logger.debug { ">> send: $message" }

            redisTemplate.convertAndSend(channel1, message).awaitSingle()
            delay(1000)
        }
    }
})

suspend fun ReactiveListOperations<Any, Any>.all(key: Any): List<Any> {
    return this.range(key, 0, -1).asFlow().toList()
}

suspend fun ReactiveZSetOperations<Any, Any>.all(key: Any): List<Any> {
    return this.range(key, Range.closed(0, -1)).asFlow().toList()
}

interface WithRedisContainer {
    companion object {
        private val container = GenericContainer(DockerImageName.parse("redis")).apply {
            addExposedPorts(6379)
            start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun setProperty(registry: DynamicPropertyRegistry) {
            logger.debug { "redis mapped port: ${container.getMappedPort(6379)}" }
            registry.add("spring.data.redis.port") {
                "${container.getMappedPort(6379)}"
            }
        }
    }
}