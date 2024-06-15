package com.fastcampus.payment.controller.view.dto

data class ReqPaySucceed(
    val paymentType: TossPaymentType,
    val orderId: String,
    val paymentKey: String,
    val amount: Long,
)
