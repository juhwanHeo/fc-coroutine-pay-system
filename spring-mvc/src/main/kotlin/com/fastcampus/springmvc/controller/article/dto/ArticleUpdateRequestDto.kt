package com.fastcampus.springmvc.controller.article.dto

data class ArticleUpdateRequestDto(
    val title: String?,
    val body: String?,
    val authorId: Long?,
)
