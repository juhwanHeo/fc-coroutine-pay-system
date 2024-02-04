package com.fastcampus.webfluxcoroutine.controller.article.dto

import com.fastcampus.webfluxcoroutine.model.Article

data class ArticleCreateRequestDto(
    val title: String,
    val body: String?,
    val authorId: Long?,
)

fun ArticleCreateRequestDto.toEntity() = Article(
    title = this.title,
    body = this.body,
    authorId = this.authorId,
)