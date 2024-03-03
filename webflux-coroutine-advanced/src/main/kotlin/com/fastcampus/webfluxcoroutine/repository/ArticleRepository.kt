package com.fastcampus.webfluxcoroutine.repository

import com.fastcampus.webfluxcoroutine.model.Article
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository: CoroutineCrudRepository<Article, Long> {

    suspend fun findAllByTitleContains(title: String): Flow<Article>
}