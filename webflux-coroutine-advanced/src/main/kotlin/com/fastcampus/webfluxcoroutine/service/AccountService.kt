package com.fastcampus.webfluxcoroutine.service

import com.fastcampus.webfluxcoroutine.controller.account.dto.AccountResponseDto
import com.fastcampus.webfluxcoroutine.controller.account.dto.toAccountResponseDto
import com.fastcampus.webfluxcoroutine.exception.NotFoundException
import kotlinx.coroutines.delay
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.fastcampus.webfluxcoroutine.repository.ArticleRepository as AccountRepository

@Service
class AccountService(
    private val accountRepository: AccountRepository,
) {

    suspend fun get(id: Long): AccountResponseDto {
        return accountRepository.findById(id)?.toAccountResponseDto() ?: throw NotFoundException("id: $id")
    }

    @Transactional
    suspend fun deposit(
        id: Long,
        amount: Long
    ) {
        accountRepository.findById(id)?.let { account ->
            delay(3000)
            account.balance += amount
            accountRepository.save(account)
        } ?: throw NotFoundException("id: $id")
    }

}