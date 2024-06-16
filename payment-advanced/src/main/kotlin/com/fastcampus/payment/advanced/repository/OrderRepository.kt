package com.fastcampus.payment.advanced.repository

import com.fastcampus.payment.advanced.model.Order
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: CoroutineCrudRepository<Order, Long>{
    suspend fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<Order>
    suspend fun findByPgOrderId(pgOrderId: String): Order?
}