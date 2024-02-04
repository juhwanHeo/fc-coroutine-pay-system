package com.fastcampus.webfluxcoroutine.controller.article.dto

data class ArticleUpdateRequestDto(
    val title: String? = null,
    val body: String? = null,
    val authorId: Long? = null,
)
