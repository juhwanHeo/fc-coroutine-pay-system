package com.fastcampus.payment.advanced.controller.order.dto

import com.fastcampus.payment.advanced.model.PgStatus
import java.time.LocalDateTime

data class ResOrder(
    val id: Long = 0,
    val userId: Long,
    val description: String? = null,
    val amount: Long = 0,
    val pgOrderId: String? = null,
    val pgKey: String? = null,
    val pgStatus: PgStatus = PgStatus.CREATE,
    val pgRetryCount: Int = 0,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val products: List<ResProductQuantity>
)