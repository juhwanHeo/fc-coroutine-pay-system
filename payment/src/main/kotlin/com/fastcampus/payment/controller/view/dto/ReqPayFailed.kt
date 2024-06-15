package com.fastcampus.payment.controller.view.dto

data class ReqPayFailed(
    val code: String,
    val message: String,
    val orderId: String,
)