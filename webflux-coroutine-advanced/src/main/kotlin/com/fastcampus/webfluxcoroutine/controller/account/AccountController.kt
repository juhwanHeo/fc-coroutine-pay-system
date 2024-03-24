package com.fastcampus.webfluxcoroutine.controller.account

import com.fastcampus.webfluxcoroutine.controller.account.dto.AccountResponseDto
import com.fastcampus.webfluxcoroutine.service.AccountService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger { }

@RestController
@RequestMapping("/account")
class AccountController(
    private val accountService: AccountService,
) {

    @GetMapping("/{id}")
    suspend fun get(
        @PathVariable id: Long,
    ): AccountResponseDto {
        return accountService.get(id)
    }

    @PutMapping("/{id}/{amount}")
    suspend fun deposit(
        @PathVariable id: Long,
        @PathVariable amount: Long,
    ): AccountResponseDto {
        accountService.deposit(id, amount)
        return accountService.get(id)
    }

    @PutMapping("/redis/{id}/{amount}")
    suspend fun depositForRedis(
        @PathVariable id: Long,
        @PathVariable amount: Long,
    ): AccountResponseDto {
        accountService.depositForRedis(id, amount)
        return accountService.get(id)
    }
}