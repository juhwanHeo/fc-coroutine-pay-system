package com.fastcampus.webfluxcoroutine.repository

import com.fastcampus.webfluxcoroutine.model.Article
import kotlinx.coroutines.flow.Flow
import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository: CoroutineCrudRepository<Article, Long> {

    suspend fun findAllByTitleContains(title: String): Flow<Article>

    @Lock(LockMode.PESSIMISTIC_WRITE)
    suspend fun findArticleById(id: Long): Article?
}