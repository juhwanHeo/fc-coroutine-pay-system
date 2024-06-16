package com.fastcampus.payment.advanced.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "TB_PROD_IN_ORDER")
class ProductInOrder(
    var prodId: Long,        // PK
    var orderId: Long,      // PK
    var price: Long,
    var quantity: Int,
    @Id
    var seq: Long = 0,
): BaseEntity() {
    override fun equals(other: Any?): Boolean = kotlinEquals(other, arrayOf(
        ProductInOrder::prodId,
        ProductInOrder::orderId,
    ))

    override fun hashCode(): Int = kotlinHashCode(arrayOf(
        ProductInOrder::prodId,
        ProductInOrder::orderId,
    ))

    override fun toString(): String = kotlinToString(arrayOf(
        ProductInOrder::prodId,
        ProductInOrder::orderId,
        ProductInOrder::price,
        ProductInOrder::quantity,
    ), superToString = { super.toString() })
}