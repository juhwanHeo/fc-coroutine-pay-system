package com.fastcampus.payment.advanced

import com.fastcampus.payment.advanced.model.Order
import com.fastcampus.payment.advanced.model.Product
import com.fastcampus.payment.advanced.model.ProductInOrder
import com.fastcampus.payment.advanced.repository.OrderRepository
import com.fastcampus.payment.advanced.repository.PIORepository
import com.fastcampus.payment.advanced.repository.ProductRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

private val logger = KotlinLogging.logger {}

@ActiveProfiles("test")
@SpringBootTest
class PaymentAdvancedApplicationTests(
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