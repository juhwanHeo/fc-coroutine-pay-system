package com.fastcampus.payment.advanced.controller.view.dto

data class ReqPaySucceed(
    val paymentType: TossPaymentType,
    val orderId: String,
    val paymentKey: String,
    val amount: Long,
)
