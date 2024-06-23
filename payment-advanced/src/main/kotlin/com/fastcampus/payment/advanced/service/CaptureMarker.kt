package com.fastcampus.payment.advanced.service

import com.fastcampus.payment.advanced.model.Order
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service

@Service
class CaptureMarker(
    template: ReactiveRedisTemplate<Any, Any>,
    @Value("\${spring.profiles.active:local}") profile: String,
    private val orderService: OrderService,
) {

    private val ops = template.opsForSet()
    private val key = "$profile/capture-marker"


    suspend fun put(orderId: Long) {
        ops.add(key, orderId).awaitSingleOrNull()
    }

    suspend fun remove(orderId: Long) {
        ops.remove(key, orderId).awaitSingleOrNull()
    }

    suspend fun getAll(): List<Order> {
        return ops.members(key)
            .asFlow()
            .map { orderService.get(it as Long) }
            .toList()
            .sortedBy { it.updatedAt }
    }
}