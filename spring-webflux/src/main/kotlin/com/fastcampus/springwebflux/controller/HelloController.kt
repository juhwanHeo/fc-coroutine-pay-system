package com.fastcampus.springwebflux.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/hello")
class HelloController {

    @GetMapping
    fun hello(
        @RequestParam name: String?
    ): Mono<String> {
        return Mono.just("Hello $name ~")
    }
}