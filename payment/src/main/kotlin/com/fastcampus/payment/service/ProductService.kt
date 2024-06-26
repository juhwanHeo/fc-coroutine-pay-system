package com.fastcampus.payment.service

import com.fastcampus.payment.config.CacheKey
import com.fastcampus.payment.config.CacheManager
import com.fastcampus.payment.model.Product
import com.fastcampus.payment.repository.ProductRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.minutes

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val cacheManager: CacheManager,
    @Value("\${spring.active.profile:local}") private val profile: String,
) {

    val CACHE_KEY = "${profile}/payment/product".also { cacheManager.ttl[it] = 10.minutes }

    suspend fun get(prodId: Long): Product? {
        val key = CacheKey(CACHE_KEY, prodId)
        return cacheManager.get(key) {
            productRepository.findById(prodId)
        }
    }
}