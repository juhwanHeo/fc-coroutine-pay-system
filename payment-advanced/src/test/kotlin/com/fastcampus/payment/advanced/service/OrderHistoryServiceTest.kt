package com.fastcampus.payment.advanced.service

import com.fastcampus.payment.advanced.config.extension.toLocalDate
import com.fastcampus.payment.advanced.controller.order.dto.QryOrderHistory
import com.fastcampus.payment.advanced.model.Order
import com.fastcampus.payment.advanced.model.PgStatus.CREATE
import com.fastcampus.payment.advanced.model.PgStatus.AUTH_SUCCESS
import com.fastcampus.payment.advanced.model.PgStatus.AUTH_INVALID
import com.fastcampus.payment.advanced.model.PgStatus.AUTH_FAIL
import com.fastcampus.payment.advanced.model.PgStatus.CAPTURE_REQUEST
import com.fastcampus.payment.advanced.model.PgStatus.CAPTURE_SUCCESS
import com.fastcampus.payment.advanced.model.PgStatus.CAPTURE_RETRY
import com.fastcampus.payment.advanced.model.PgStatus.CAPTURE_FAIL
import com.fastcampus.payment.advanced.repository.OrderRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test")
class OrderHistoryServiceTest(
    @Autowired private val orderHistoryService: OrderHistoryService,
    @Autowired private val orderRepository: OrderRepository,
): StringSpec({

    afterTest {
        orderRepository.deleteAll()
    }

    "filter valid status" {
        listOf(
            Order(userId = 11, description = "a", amount = 1000, pgStatus = CREATE),
            Order(userId = 11, description = "a", amount = 1000, pgStatus = AUTH_SUCCESS),
            Order(userId = 11, description = "a", amount = 1000, pgStatus = AUTH_FAIL),
            Order(userId = 11, description = "a", amount = 1000, pgStatus = AUTH_INVALID),
            Order(userId = 11, description = "a", amount = 1000, pgStatus = CAPTURE_REQUEST),   //  조회됨
            Order(userId = 11, description = "a", amount = 1000, pgStatus = CAPTURE_RETRY),     //  조회됨
            Order(userId = 11, description = "a", amount = 1000, pgStatus = CAPTURE_SUCCESS),   //  조회됨
            Order(userId = 11, description = "a", amount = 1000, pgStatus = CAPTURE_FAIL),
        ).forEach { orderRepository.save(it) }

        orderHistoryService.getHistories(QryOrderHistory(userId = 11)).size shouldBe 3
    }

    "get order history" {
        var createdAt = "2024-01-01".toLocalDate().atStartOfDay()

        listOf(
            Order(userId = 11, description = "A,B",     amount = 1000), // 2024-01-01
            Order(userId = 11, description = "C",       amount = 1100), // 2024-01-02
            Order(userId = 11, description = "D,E,F",   amount = 1200), // 2024-01-03
            Order(userId = 11, description = "D,G,H",   amount = 1300), // 2024-01-04
            Order(userId = 11, description = "I,J",     amount = 1400), // 2024-01-05
            Order(userId = 11, description = "I,K",     amount = 1500), // 2024-01-06
            Order(userId = 11, description = "I,L,M",   amount = 1600), // 2024-01-07
            Order(userId = 11, description = "I,L,M,N", amount = 1700), // 2024-01-08
            Order(userId = 11, description = "O",       amount = 1800), // 2024-01-09
            Order(userId = 11, description = "P,Q",     amount = 1900), // 2024-01-10
            Order(userId = 11, description = "P,R",     amount = 2000), // 2024-01-11
        ).forEach {
            it.pgStatus = CAPTURE_SUCCESS
            orderRepository.save(it)

            it.createdAt = createdAt
            createdAt = createdAt.plusDays(1)
            orderRepository.save(it)
        }

        orderRepository.count().let { logger.debug { ">> count: $it" } }
        orderRepository.findAll().toList().let { logger.debug { it.joinToString("\n") } }

        // init
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, limit = 20)).size shouldBe 11

        // keyword
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "A")).size shouldBe 1
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "B")).size shouldBe 1
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "C")).size shouldBe 1
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "A")).first().id shouldBe
            orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "B")).first().id
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "D")).size shouldBe 2
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "D, H")).size shouldBe 1
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "I")).size shouldBe 4
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "I K")).size shouldBe 1
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "I L")).size shouldBe 2
        orderHistoryService.getHistories(QryOrderHistory(userId = 11, keyword = "I L N")).size shouldBe 1

        // page
        orderHistoryService.getHistories(QryOrderHistory(userId=11, limit=2)).size shouldBe 2
        orderHistoryService.getHistories(QryOrderHistory(userId=11, limit=2, page=2)).first()
        orderHistoryService.getHistories(QryOrderHistory(userId=11, limit=2, page=2)).first().id shouldBe
            orderHistoryService.getHistories(QryOrderHistory(userId=11, limit=3, page=1)).last().id
        orderHistoryService.getHistories(QryOrderHistory(userId=11, limit=5, page=3)).size shouldBe 1

        // amount
        orderHistoryService.getHistories(QryOrderHistory(userId=11, keyword="I", fromAmount=1450)).size shouldBe 3
        orderHistoryService.getHistories(QryOrderHistory(userId=11, keyword="I", fromAmount=1450, toAmount=1650)).size shouldBe 2

        // date
        orderHistoryService.getHistories(QryOrderHistory(userId=11, fromDate="2024-01-03")).size shouldBe 9
        orderHistoryService.getHistories(QryOrderHistory(userId=11, fromDate="2024-01-03", toDate="2024-01-08")).size shouldBe 6
    }
})