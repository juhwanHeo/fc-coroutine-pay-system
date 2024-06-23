package com.fastcampus.payment.advanced.controller.order

import com.fastcampus.payment.advanced.controller.order.dto.QryOrderHistory
import com.fastcampus.payment.advanced.controller.order.dto.ReqCreateOrderDto
import com.fastcampus.payment.advanced.controller.order.dto.ResOrder
import com.fastcampus.payment.advanced.model.Order
import com.fastcampus.payment.advanced.model.toResOrder
import com.fastcampus.payment.advanced.service.CaptureMarker
import com.fastcampus.payment.advanced.service.OrderHistoryService
import com.fastcampus.payment.advanced.service.OrderService
import com.fastcampus.payment.advanced.service.PaymentService
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService,
    private val orderHistoryService: OrderHistoryService,
    private val paymentService: PaymentService,
    private val captureMarker: CaptureMarker,
) {

    @GetMapping("/{orderId}")
    suspend fun get(
        @PathVariable orderId: Long,
    ): ResOrder {
        return orderService.get(orderId).toResOrder()
    }

    @GetMapping("/all/{userId}")
    suspend fun getAllByUserId(
        @PathVariable userId: Long,
    ): List<ResOrder> {
        return orderService.getAllByUserId(userId).map { it.toResOrder() }
    }

    @PostMapping
    suspend fun create(
        @RequestBody createOrderDto: ReqCreateOrderDto
    ): ResOrder {
        return orderService.create(createOrderDto).toResOrder()
    }

    @DeleteMapping("/{orderId}")
    suspend fun delete(
        @PathVariable orderId: Long,
    ) {
        orderService.deleteById(orderId)
    }

    @GetMapping("/history")
    suspend fun history(request: QryOrderHistory): List<Order> {
        return orderHistoryService.getHistories(request)
    }

    @PutMapping("/recapture/{orderId}")
    suspend fun recapture(@PathVariable orderId: Long) {
        orderService.get(orderId).let { order ->
            logger.debug { ">> recapture $order" }
            // temp = 2 ^ retry count
            // delay = (temp / 2) + (0.. (temp/2)).random
            delay(getBackoffDelay(order).also { logger.debug { ">> delay: $it ms" } })

            paymentService.capture(order)
        }
    }

    private fun getBackoffDelay(order: Order): Duration {
        val temp = (2.0).pow(order.pgRetryCount).toInt() * 1000
        val delay = temp + (0 .. temp).random()
        return delay.milliseconds
    }

    @GetMapping("/capturing")
    suspend fun getCapturingOrder(): List<Order> {
        return captureMarker.getAll()
    }
}
