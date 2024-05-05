package com.fastcampus.payment.repository

import com.fastcampus.payment.model.ProductInOrder
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PIORepository: CoroutineCrudRepository<ProductInOrder, Long> {
    suspend fun countByOrderId(orderId: Long): Long
    suspend fun findAllByOrderId(orderId: Long): List<ProductInOrder>
}