package com.fastcampus.payment.service

import com.fastcampus.payment.controller.order.dto.ReqCreateOrderDto
import com.fastcampus.payment.controller.order.dto.ReqProdQuantity
import com.fastcampus.payment.exception.ProductNotFound
import com.fastcampus.payment.model.Product
import com.fastcampus.payment.repository.PIORepository
import com.fastcampus.payment.repository.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

private val logger = KotlinLogging.logger {}

@ActiveProfiles("test")
@SpringBootTest
class OrderServiceTest(
    @Autowired private val orderService: OrderService,
    @Autowired private val productRepository: ProductRepository,
    @Autowired private val productInOrderRepository: PIORepository,
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
})
