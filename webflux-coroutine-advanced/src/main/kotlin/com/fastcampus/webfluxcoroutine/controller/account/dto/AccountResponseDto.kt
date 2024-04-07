package com.fastcampus.webfluxcoroutine.controller.account.dto

import com.fastcampus.webfluxcoroutine.model.Article

data class AccountResponseDto(
    val id: Long,
    val balance: Long,
)

fun Article.toAccountResponseDto(): AccountResponseDto {
    return AccountResponseDto(
        id = this.id,
        balance = this.balance,
    )
}