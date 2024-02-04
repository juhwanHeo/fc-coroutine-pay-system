package com.fastcampus.webfluxcoroutine.service

import com.fastcampus.webfluxcoroutine.controller.article.dto.ArticleCreateRequestDto
import com.fastcampus.webfluxcoroutine.controller.article.dto.ArticleUpdateRequestDto
import com.fastcampus.webfluxcoroutine.controller.article.dto.toEntity
import com.fastcampus.webfluxcoroutine.exception.NotFoundException
import com.fastcampus.webfluxcoroutine.model.Article
import com.fastcampus.webfluxcoroutine.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
) {

    suspend fun create(
        requestDto: ArticleCreateRequestDto
    ): Article {
        return articleRepository.save(requestDto.toEntity())
    }

    suspend fun get(
        id: Long,
    ): Article {
        return articleRepository.findById(id) ?: throw NotFoundException("not found article by id: $id")
    }

    suspend fun getAll(
        title: String? = null
    ): Flow<Article> {
        return if (title.isNullOrBlank()) {
            articleRepository.findAll()
        }
        else {
            articleRepository.findAllByTitleContains(title)
        }
    }

    suspend fun update(
        id: Long,
        requestDto: ArticleUpdateRequestDto,
    ): Article {
        val article = articleRepository.findById(id) ?: throw NotFoundException("not found article by id: $id")
        return articleRepository.save(article.apply {
            requestDto.title?.let { this.title = it }
            requestDto.body?.let { this.body = it }
            requestDto.authorId?.let { this.authorId = it }
        })
    }

    suspend fun delete(
        id: Long,
    ) {
        return articleRepository.deleteById(id)
    }
}