package com.fastcampus.payment.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.fastcampus.payment.common.Beans.Companion.beanProductInOrderRepository
import com.fastcampus.payment.common.Beans.Companion.beanProductService
import com.fastcampus.payment.controller.order.dto.ResOrder
import com.fastcampus.payment.controller.order.dto.ResProductQuantity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("TB_ORDER")
class Order(
    @Id
    var id: Long = 0,
    var userId: Long,
    var description: String? = null,
    var amount: Long = 0,
    var pgOrderId: String? = null,
    var pgKey: String? = null,
    var pgStatus: PgStatus = PgStatus.CREATE,
    var pgRetryCount: Int = 0,
): BaseEntity() {

    override fun equals(other: Any?): Boolean = kotlinEquals(other, arrayOf(
        Order::id,
    ))

    override fun hashCode(): Int = kotlinHashCode(arrayOf(
        Order::id,
    ))

    override fun toString(): String = kotlinToString(arrayOf(
        Order::id,
        Order::userId,
        Order::description,
        Order::amount,
        Order::pgOrderId,
        Order::pgKey,
        Order::pgStatus,
        Order::pgRetryCount,
    ), superToString = { super.toString() })
}

suspend fun Order.toResOrder(): ResOrder {
    return this.let {
        ResOrder(
            id = it.id,
            userId = it.userId,
            description = it.description,
            amount = it.amount,
            pgOrderId = it.pgOrderId,
            pgKey = it.pgKey,
            pgStatus = it.pgStatus,
            pgRetryCount = it.pgRetryCount,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt,
            products = beanProductInOrderRepository.findAllByOrderId(it.id).map { prodInOrd ->
                ResProductQuantity(
                    id = prodInOrd.prodId,
                    name = beanProductService.get(prodInOrd.prodId)?.name ?: "unknown",
                    price = prodInOrd.price,
                    quantity = prodInOrd.quantity,
                )
            },
        )
    }
}
