package com.fastcampus.springwebflux.controller.article.dto

import java.time.LocalDateTime

data class ArticleCreateResponseDto(
    val id: Long,
    val title: String,
    val body: String?,
    val authorId: Long?,
    val createdAt: LocalDateTime?,
)