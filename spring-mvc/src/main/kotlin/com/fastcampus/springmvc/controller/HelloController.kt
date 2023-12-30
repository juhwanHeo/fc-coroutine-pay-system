package com.fastcampus.springmvc.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hello")
class HelloController {

    @GetMapping
    fun hello(
        @RequestParam name: String?,
    ): String {
        return "hello $name ~"
    }

    @GetMapping("/{name}")
    fun helloPath(
        @PathVariable name: String?,
    ): String {
        return "hello $name ~"
    }
}