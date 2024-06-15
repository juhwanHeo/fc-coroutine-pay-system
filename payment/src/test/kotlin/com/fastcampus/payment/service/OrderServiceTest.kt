package com.fastcampus.payment.service

import com.fastcampus.payment.controller.order.dto.ReqCreateOrderDto
import com.fastcampus.payment.controller.order.dto.ReqProdQuantity
import com.fastcampus.payment.controller.view.dto.ReqPaySucceed
import com.fastcampus.payment.controller.view.dto.TossPaymentType
import com.fastcampus.payment.exception.ProductNotFound
import com.fastcampus.payment.model.PgStatus
import com.fastcampus.payment.model.Product
import com.fastcampus.payment.repository.PIORepository
import com.fastcampus.payment.repository.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import mu.KotlinLogging
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

private val logger = KotlinLogging.logger {}

@ActiveProfiles("test", "toss-pay-test")
@SpringBootTest
class OrderServiceTest(
    @Autowired private val orderService: OrderService,
    @Autowired private val productRepository: ProductRepository,
    @Autowired private val productInOrderRepository: PIORepository,
    @Autowired private val tossPayApi: TossPayApi,
) : StringSpec({

    beforeTest {
        val products = listOf(
            Product(1, "apple", 1000).apply { new = true },
            Product(2, "banana", 1200).apply { new = true },
            Product(3, "mango", 700).apply { new = true },
            Product(4, "orange", 2100).apply { new = true },
        )
        productRepository.save(products[0])
        productRepository.save(products[1])
        productRepository.save(products[2])
        productRepository.save(products[3])
    }

    afterTest {
        productRepository.deleteAll()
    }

    "create order on fail" {
        val reqCreateOrderDto = ReqCreateOrderDto(11, listOf(
                ReqProdQuantity(1, 1),
                ReqProdQuantity(2, 2),
                ReqProdQuantity(3, 3),
                ReqProdQuantity(4, 4),
                ReqProdQuantity(5, 5),
        ))

        shouldThrow<ProductNotFound> {
            orderService.create(reqCreateOrderDto)
        }
    }

    "create order" {
        val reqCreateOrderDto = ReqCreateOrderDto(11, listOf(
            ReqProdQuantity(1, 1),
            ReqProdQuantity(2, 2),
            ReqProdQuantity(3, 3),
            ReqProdQuantity(4, 4),
        ))
        val order = orderService.create(reqCreateOrderDto).also { logger.debug { it } }

        order.amount shouldBe 13900
        order.description shouldNotBe null
        order.pgOrderId shouldNotBe null

        productInOrderRepository.countByOrderId(order.id) shouldBe 4
    }

    "capture success" {
        val order = orderService.create(ReqCreateOrderDto(11, listOf(ReqProdQuantity(1, 10))))
        order.pgStatus shouldBe PgStatus.CREATE

        val token = ReqPaySucceed(TossPaymentType.NORMAL, order.pgOrderId!!, "test idempotency key", order.amount)

        // auth
        orderService.authSucceed(token)
        val orderAuthed = orderService.get(order.id).also { it.pgStatus shouldBe PgStatus.AUTH_SUCCESS }

        Mockito.`when`(tossPayApi.confirm(token)).thenReturn(ResConfirm(
            paymentKey = orderAuthed.pgKey!!,
            orderId = orderAuthed.pgOrderId!!,
            status = "Done",
            totalAmount = orderAuthed.amount,
            method = "card"
        ))

        // capture
        orderService.capture(token)
        orderService.get(order.id).also { it.pgStatus shouldBe PgStatus.CAPTURE_SUCCESS }
    }

    "capture retry" {
        val order = orderService.create(ReqCreateOrderDto(11, listOf(ReqProdQuantity(1, 10))))
        order.pgStatus shouldBe PgStatus.CREATE

        val token = ReqPaySucceed(TossPaymentType.NORMAL, order.pgOrderId!!, "test idempotency key", order.amount)

        // auth
        orderService.authSucceed(token)
        orderService.get(order.id).also { it.pgStatus shouldBe PgStatus.AUTH_SUCCESS }

        Mockito.`when`(tossPayApi.confirm(token))
            .thenThrow(WebClientRequestException::class.java)

        // capture
        orderService.capture(token)
        orderService.get(order.id).also { it.pgStatus shouldBe PgStatus.CAPTURE_RETRY }
    }

    "capture fail" {
        val order = orderService.create(ReqCreateOrderDto(11, listOf(ReqProdQuantity(1, 10))))
        order.pgStatus shouldBe PgStatus.CREATE

        val token = ReqPaySucceed(TossPaymentType.NORMAL, order.pgOrderId!!, "test idempotency key", order.amount)

        // auth
        orderService.authSucceed(token)
        orderService.get(order.id).also { it.pgStatus shouldBe PgStatus.AUTH_SUCCESS }

        Mockito.`when`(tossPayApi.confirm(token))
            .thenThrow(WebClientResponseException::class.java)

        // capture
        orderService.capture(token)
        orderService.get(order.id).also { it.pgStatus shouldBe PgStatus.CAPTURE_FAIL }
    }
})
