package com.fastcampus.payment.repository

import com.fastcampus.payment.model.Order
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: CoroutineCrudRepository<Order, Long>{
    suspend fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<Order>
}