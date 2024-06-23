package com.fastcampus.payment.advanced.controller.order.dto

data class ReqCreateOrderDto(
    val userId: Long,
    val products: List<ReqProdQuantity>,
)