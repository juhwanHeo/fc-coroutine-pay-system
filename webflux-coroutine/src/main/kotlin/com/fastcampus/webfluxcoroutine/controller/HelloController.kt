package com.fastcampus.webfluxcoroutine.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/")
    suspend fun index(

    ): String {
        return "index page"
    }

    @GetMapping("/hello")
    suspend fun hello(
        @RequestParam name: String?
    ) : String {
        return "hello world $name"
    }
}
