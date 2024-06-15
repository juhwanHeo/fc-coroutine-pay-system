package com.fastcampus.payment.controller.order

import com.fastcampus.payment.controller.order.dto.QryOrderHistory
import com.fastcampus.payment.controller.order.dto.ReqCreateOrderDto
import com.fastcampus.payment.controller.order.dto.ResOrder
import com.fastcampus.payment.model.Order
import com.fastcampus.payment.model.toResOrder
import com.fastcampus.payment.service.OrderHistoryService
import com.fastcampus.payment.service.OrderService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService,
    private val orderHistoryService: OrderHistoryService,
) {

    @GetMapping("/{orderId}")
    suspend fun get(
        @PathVariable orderId: Long,
    ): ResOrder {
        return orderService.get(orderId).toResOrder()
    }

    @GetMapping("/all/{userId}")
    suspend fun getAllByUserId(
        @PathVariable userId: Long,
    ): List<ResOrder> {
        return orderService.getAllByUserId(userId).map { it.toResOrder() }
    }

    @PostMapping
    suspend fun create(
        @RequestBody createOrderDto: ReqCreateOrderDto
    ): ResOrder {
        return orderService.create(createOrderDto).toResOrder()
    }

    @DeleteMapping("/{orderId}")
    suspend fun delete(
        @PathVariable orderId: Long,
    ) {
        orderService.deleteById(orderId)
    }

    @GetMapping("/history")
    suspend fun history(request: QryOrderHistory): List<Order> {
        return orderHistoryService.getHistories(request)
    }
}
