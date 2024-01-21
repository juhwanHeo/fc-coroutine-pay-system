package com.fastcampus.springwebflux.repository

import com.fastcampus.springwebflux.model.Article
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface ArticleRepository: R2dbcRepository<Article, Long> {
    fun findAllByTitleContains(title: String): Flux<Article>
}