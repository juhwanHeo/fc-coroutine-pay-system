package com.fastcampus.payment.controller.view

import com.fastcampus.payment.model.toResOrder
import com.fastcampus.payment.service.OrderService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


@Controller
class ViewController(
    private val orderService: OrderService,
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
}