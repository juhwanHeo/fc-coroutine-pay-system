package com.fastcampus.payment.advanced.controller.view

import com.fastcampus.payment.advanced.controller.view.dto.ReqPayFailed
import com.fastcampus.payment.advanced.controller.view.dto.ReqPaySucceed
import com.fastcampus.payment.advanced.model.toResOrder
import com.fastcampus.payment.advanced.service.OrderService
import com.fastcampus.payment.advanced.service.PaymentService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


@Controller
class ViewController(
    private val orderService: OrderService,
    private val paymentService: PaymentService,
) {

    @GetMapping("/hello/{name}")
    suspend fun hello(
        @PathVariable("name") name: String,
        model: Model,
    ): String {

        model.addAttribute("pname", name)
        model.addAttribute("order", orderService.get(1).toResOrder())
        return "hello-world.html"
    }

    @GetMapping("/pay/{orderId}")
    suspend fun pay(
        @PathVariable("orderId") orderId: Long,
        model: Model,
    ): String {
        val order = orderService.get(orderId)
        model.addAttribute("order", order)
        return "pay.html"
    }

    @GetMapping("/pay/success")
    suspend fun successPay(
        request: ReqPaySucceed,
    ): String {
        if (!paymentService.authSucceed(request))
            return "pay-fail.html"

        paymentService.capture(request)
        return "pay-success.html"
    }

    @GetMapping("/pay/fail")
    suspend fun failPay(
        request: ReqPayFailed,
    ): String {
        paymentService.authFailed(request)
        return "pay-fail.html"
    }
}