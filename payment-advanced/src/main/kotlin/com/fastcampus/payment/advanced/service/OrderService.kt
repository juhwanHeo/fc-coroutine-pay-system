package com.fastcampus.payment.advanced.service

import com.fastcampus.payment.advanced.controller.order.dto.ReqCreateOrderDto
import com.fastcampus.payment.advanced.exception.OrderNotFound
import com.fastcampus.payment.advanced.exception.ProductNotFound
import com.fastcampus.payment.advanced.model.Order
import com.fastcampus.payment.advanced.model.PgStatus
import com.fastcampus.payment.advanced.model.ProductInOrder
import com.fastcampus.payment.advanced.repository.OrderRepository
import com.fastcampus.payment.advanced.repository.PIORepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productService: ProductService,
    private val productInOrderRepository: PIORepository,
) {

    @Transactional
    suspend fun create(requestDto: ReqCreateOrderDto): Order {
        val prodIds = requestDto.products.map { it. prodId }.toSet()
        val productsById = requestDto.products.mapNotNull { productService.get(it.prodId) }.associateBy { it.id }

        logger.info { "productIds: $prodIds" }
        logger.info { "productsById: $productsById" }

        prodIds.filter { !productsById.containsKey(it) }
            .let { remains ->
                if (remains.isNotEmpty()) throw ProductNotFound("Product not found: $remains")
            }

        val amount = requestDto.products.sumOf { productsById[it.prodId]!!.price * it.quantity }
        val description = requestDto.products.joinToString(", ") { "${productsById[it.prodId]!!.name} x ${it.quantity}" }

        val newOrder = orderRepository.save(Order(
            userId = requestDto.userId,
            description = description,
            amount =  amount,
            pgOrderId = "${UUID.randomUUID()}".replace("-", ""),
            pgStatus = PgStatus.CREATE,
        ))

        requestDto.products.forEach {
            productInOrderRepository.save(ProductInOrder(
                orderId = newOrder.id,
                prodId = it.prodId,
                price = productsById[it.prodId]!!.price,
                quantity = it.quantity,
            ))
        }

        return newOrder
    }

    suspend fun get(orderId: Long): Order {
        return orderRepository.findById(orderId) ?: throw OrderNotFound("Order not found: $orderId")
    }

    suspend fun getAllByUserId(userId: Long): List<Order> {
        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
    }

    suspend fun deleteById(orderId: Long) {
        orderRepository.deleteById(orderId)
    }

    suspend fun getOrderByPgOrderId(pgOrderId: String): Order {
        return orderRepository.findByPgOrderId(pgOrderId)
            ?: throw OrderNotFound("Order not found by pgOrderId: $pgOrderId")
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    suspend fun save(order: Order): Order {
        return orderRepository.save(order)
    }
}
