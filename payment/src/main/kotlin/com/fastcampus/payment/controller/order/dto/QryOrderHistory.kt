package com.fastcampus.payment.controller.order.dto

class QryOrderHistory(
    val userId: Long,
    val keyword: String? = null,
    val fromDate: String? = null,
    val toDate: String? = null,
    val fromAmount: Long? = null,
    val toAmount: Long? = null,
    val limit: Int = 10,
    val page: Int = 1,
)