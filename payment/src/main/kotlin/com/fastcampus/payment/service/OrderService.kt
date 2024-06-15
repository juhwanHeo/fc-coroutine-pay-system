package com.fastcampus.payment.service

import com.fastcampus.payment.common.Beans.Companion.beanOrderService
import com.fastcampus.payment.controller.order.dto.ReqCreateOrderDto
import com.fastcampus.payment.controller.view.dto.ReqPayFailed
import com.fastcampus.payment.controller.view.dto.ReqPaySucceed
import com.fastcampus.payment.exception.OrderNotFound
import com.fastcampus.payment.exception.ProductNotFound
import com.fastcampus.payment.model.Order
import com.fastcampus.payment.model.PgStatus
import com.fastcampus.payment.model.ProductInOrder
import com.fastcampus.payment.repository.OrderRepository
import com.fastcampus.payment.repository.PIORepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productService: ProductService,
    private val productInOrderRepository: PIORepository,
    private val tossPayApi: TossPayApi,
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

    @Transactional
    suspend fun authSucceed(request: ReqPaySucceed): Boolean {
        logger.debug { "authSucceed request: $request" }
        val order = getOrderByPgOrderId(request.orderId).apply {
            pgKey = request.paymentKey
            pgStatus = PgStatus.AUTH_SUCCESS
        }

        try {
            // 사용자가 악의 적으로 금액을 다르게 수정 후 요청 했을 경우를 validation 하는 과정
            if (order.amount != request.amount) {
                order.pgStatus = PgStatus.AUTH_INVALID
                logger.error { "Invalid auth because of amount (order: ${order.amount}, pay: ${request.amount})" }
                return false
            }
            return true
        } finally {
            orderRepository.save(order)
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    suspend fun save(order: Order): Order {
        return orderRepository.save(order)
    }

    @Transactional
    suspend fun capture(request: ReqPaySucceed): Boolean {
        logger.debug { "capture request: $request" }
        val order = getOrderByPgOrderId(request.orderId).apply {
            pgStatus = PgStatus.CAPTURE_REQUEST
            beanOrderService.save(this)
        }

        logger.debug { ">>> order: $order" }
        return try {
            tossPayApi.confirm(request).also { logger.debug { ">>> res: $it" } }
            order.pgStatus = PgStatus.CAPTURE_SUCCESS
            true
        } catch (e: Exception) {
            logger.error(e.message, e)
            order.pgStatus = when (e) {
                is WebClientRequestException -> PgStatus.CAPTURE_RETRY
                is WebClientResponseException -> PgStatus.CAPTURE_FAIL
                else -> PgStatus.CAPTURE_FAIL
            }
            false
        } finally {
            orderRepository.save(order)
        }
    }

    @Transactional
    suspend fun authFailed(request: ReqPayFailed) {
        val order = getOrderByPgOrderId(request.orderId)

        if (order.pgStatus == PgStatus.CREATE) {
            order.pgStatus = PgStatus.AUTH_FAIL
            orderRepository.save(order)
        }
        logger.error { """
                >>> Fail on error
                    - request: $request
                    - order  : $order
            """.trimIndent()
        }
    }
}
