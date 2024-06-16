package com.fastcampus.payment.advanced.controller.view.dto

data class ReqPayFailed(
    val code: String,
    val message: String,
    val orderId: String,
)