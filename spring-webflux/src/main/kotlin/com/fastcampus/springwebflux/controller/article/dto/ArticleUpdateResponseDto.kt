package com.fastcampus.springwebflux.controller.article.dto

import java.time.LocalDateTime

data class ArticleUpdateResponseDto (
    val title: String?,
    val body: String?,
    val authorId: Long?,
    val updatedAt: LocalDateTime?,
)