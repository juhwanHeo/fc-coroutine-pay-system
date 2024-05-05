package com.fastcampus.payment.controller.order.dto

data class ReqCreateOrderDto(
    val userId: Long,
    val products: List<ReqProdQuantity>,
)