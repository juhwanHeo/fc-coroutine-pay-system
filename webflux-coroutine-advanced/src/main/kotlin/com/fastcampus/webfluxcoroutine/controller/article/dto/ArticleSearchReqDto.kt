package com.fastcampus.webfluxcoroutine.controller.article.dto

import com.fastcampus.webfluxcoroutine.config.validator.DateString
import java.io.Serializable

data class ArticleSearchReqDto(
    val title: String?,
    val authorId: List<Long>?,

    @DateString
    val from: String?,
    @DateString
    val to: String?,
): Serializable
