package com.fastcampus.payment.advanced.service

import com.fastcampus.payment.advanced.common.Beans.Companion.beanOrderService
import com.fastcampus.payment.advanced.controller.view.dto.ReqPayFailed
import com.fastcampus.payment.advanced.controller.view.dto.ReqPaySucceed
import com.fastcampus.payment.advanced.controller.view.dto.TossPaymentType
import com.fastcampus.payment.advanced.exception.InvalidOrderStatus
import com.fastcampus.payment.advanced.model.Order
import com.fastcampus.payment.advanced.model.PgStatus.AUTH_FAIL
import com.fastcampus.payment.advanced.model.PgStatus.AUTH_INVALID
import com.fastcampus.payment.advanced.model.PgStatus.AUTH_SUCCESS
import com.fastcampus.payment.advanced.model.PgStatus.CAPTURE_FAIL
import com.fastcampus.payment.advanced.model.PgStatus.CAPTURE_REQUEST
import com.fastcampus.payment.advanced.model.PgStatus.CAPTURE_RETRY
import com.fastcampus.payment.advanced.model.PgStatus.CAPTURE_SUCCESS
import com.fastcampus.payment.advanced.model.PgStatus.CREATE
import com.fastcampus.payment.advanced.service.api.PaymentApi
import com.fastcampus.payment.advanced.service.api.TossPayApi
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.Duration
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Service
class PaymentService(
    private val orderService: OrderService,
    private val tossPayApi: TossPayApi,
    private val paymentApi: PaymentApi,
    private val captureMarker: CaptureMarker,
    private val objectMapper: ObjectMapper,
) {

    @Transactional
    suspend fun authSucceed(request: ReqPaySucceed): Boolean {
        logger.debug { "authSucceed request: $request" }
        val order = orderService.getOrderByPgOrderId(request.orderId).apply {
            pgKey = request.paymentKey
            pgStatus = AUTH_SUCCESS
        }

        try {
            // 사용자가 악의 적으로 금액을 다르게 수정 후 요청 했을 경우를 validation 하는 과정
            if (order.amount != request.amount) {
                order.pgStatus = AUTH_INVALID
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

        if (order.pgStatus == CREATE) {
            order.pgStatus = AUTH_FAIL
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
    suspend fun capture(request: ReqPaySucceed) {
        logger.debug { "capture request: $request" }
        val order = orderService.getOrderByPgOrderId(request.orderId).apply {
            pgStatus = CAPTURE_REQUEST
            beanOrderService.save(this)
        }
        capture(order)
    }

    @Transactional
    suspend fun capture(order: Order) {
        logger.debug { ">>> order: $order" }

        // capture, retry 상태인 Order 만 capture 진행
        if (order.pgStatus !in setOf(CAPTURE_REQUEST, CAPTURE_RETRY))
            throw InvalidOrderStatus("invalid order status (orderId: ${order.id}, status: ${order.pgStatus})")

        order.increaseRetryCount()

        captureMarker.put(order.id)
        try {
            tossPayApi.confirm(order.toReqPaySucceed()).also { logger.debug { ">>> res: $it" } }
            order.pgStatus = CAPTURE_SUCCESS
        } catch (e: Exception) {
            logger.error(e.message, e)
            order.pgStatus = when (e) {
                is WebClientRequestException -> CAPTURE_RETRY
                is WebClientResponseException -> {
                    val tossApiError = e.toTossPayApiError()
                    logger.debug { ">> toss api error: $tossApiError" }

                    when (tossApiError.code) {
                        "ALREADY_PROCESSED_PAYMENT" -> CAPTURE_SUCCESS
                        "PROVIDER_ERROR", "FAILED_INTERNAL_SYSTEM_PROCESSING" -> CAPTURE_RETRY
                        else -> CAPTURE_FAIL
                    }
                }
                else -> CAPTURE_FAIL
            }

            if (order.pgStatus == CAPTURE_RETRY && order.pgRetryCount >= 3)
                order.pgStatus = CAPTURE_FAIL
            if (order.pgStatus != CAPTURE_SUCCESS)
                throw e
        } finally {
            orderService.save(order)
            captureMarker.remove(order.id)
            if (order.pgStatus == CAPTURE_RETRY) {
                paymentApi.recapture(order.id)
            }
        }
    }

    suspend fun recaptureOnBoot() {
        val now = LocalDateTime.now()
        captureMarker.getAll()
            .filter { Duration.between(it.updatedAt!!, now).seconds >= 60 }
            .forEach { order -> paymentApi.recapture(order.id) }
    }

    private fun Order.toReqPaySucceed(): ReqPaySucceed {
        return this.let {
            ReqPaySucceed(
                paymentKey = it.pgKey!!,
                orderId = it.pgOrderId!!,
                amount = it.amount,
                paymentType = TossPaymentType.NORMAL,
            )
        }
    }

    private fun WebClientResponseException.toTossPayApiError(): TossPayApiError {
        val json = String(this.responseBodyAsByteArray)
        return objectMapper.readValue(json, TossPayApiError::class.java)
    }
}

data class TossPayApiError(
    val code: String,
    val message: String,
)