package com.fastcampus.payment.service

import com.fastcampus.payment.common.Beans.Companion.beanOrderService
import com.fastcampus.payment.controller.view.dto.ReqPayFailed
import com.fastcampus.payment.controller.view.dto.ReqPaySucceed
import com.fastcampus.payment.model.PgStatus
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

private val logger = KotlinLogging.logger {}

@Service
class PaymentService(
    private val orderService: OrderService,
    private val tossPayApi: TossPayApi,
) {

    @Transactional
    suspend fun authSucceed(request: ReqPaySucceed): Boolean {
        logger.debug { "authSucceed request: $request" }
        val order = orderService.getOrderByPgOrderId(request.orderId).apply {
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
            orderService.save(order)
        }
    }

    @Transactional
    suspend fun authFailed(request: ReqPayFailed) {
        val order = orderService.getOrderByPgOrderId(request.orderId)

        if (order.pgStatus == PgStatus.CREATE) {
            order.pgStatus = PgStatus.AUTH_FAIL
            orderService.save(order)
        }
        logger.error { """
                >>> Fail on error
                    - request: $request
                    - order  : $order
            """.trimIndent()
        }
    }

    @Transactional
    suspend fun capture(request: ReqPaySucceed): Boolean {
        logger.debug { "capture request: $request" }
        val order = orderService.getOrderByPgOrderId(request.orderId).apply {
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
            orderService.save(order)
        }
    }
}