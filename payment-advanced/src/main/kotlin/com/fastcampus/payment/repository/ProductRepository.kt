package com.fastcampus.payment.repository

import com.fastcampus.payment.model.Product
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: CoroutineCrudRepository<Product, Long> {
}