package com.fastcampus.springmvc.controller.article.dto

import com.fastcampus.springmvc.model.Article

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