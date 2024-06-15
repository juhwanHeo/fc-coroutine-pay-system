package com.fastcampus.payment

import com.fastcampus.payment.model.Order
import com.fastcampus.payment.model.Product
import com.fastcampus.payment.model.ProductInOrder
import com.fastcampus.payment.repository.OrderRepository
import com.fastcampus.payment.repository.PIORepository
import com.fastcampus.payment.repository.ProductRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

private val logger = KotlinLogging.logger {}

@ActiveProfiles("test")
@SpringBootTest
class PaymentApplicationTests(
	@Autowired prodRepository: ProductRepository,
	@Autowired orderRepository: OrderRepository,
	@Autowired prodInOrderRepository: PIORepository,
): StringSpec({
	"product" {
		val prevCnt = prodRepository.count()
		prodRepository.save(Product(1,"a",1000).apply { new = true })
		val currCnt = prodRepository.count()
		currCnt shouldBe prevCnt + 1
	}
	"order" {
		val prevCnt = orderRepository.count()
		orderRepository.save(Order(userId = 1)).also { logger.debug { it } }
		val currCnt = orderRepository.count()
		currCnt shouldBe prevCnt + 1
	}
	"prod in order" {
		val prevCnt = prodInOrderRepository.count()
		prodInOrderRepository.save(ProductInOrder(1,1,1,1)).also { logger.debug { it } }
		val currCnt = prodInOrderRepository.count()
		currCnt shouldBe prevCnt + 1
	}
})