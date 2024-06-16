package com.fastcampus.payment.advanced.controller.order.dto

data class ResProductQuantity(
    val id: Long,
    val name: String,
    val price: Long,
    val quantity: Int,
)