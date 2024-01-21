package com.fastcampus.springwebflux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@EnableR2dbcAuditing
@SpringBootApplication
class SpringWebfluxApplication

fun main(args: Array<String>) {
	runApplication<SpringWebfluxApplication>(*args)
}
