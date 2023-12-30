package com.fastcampus.springmvc.service

import com.fastcampus.springmvc.controller.article.dto.ArticleCreateRequestDto
import com.fastcampus.springmvc.controller.article.dto.ArticleUpdateRequestDto
import com.fastcampus.springmvc.controller.article.dto.toEntity
import com.fastcampus.springmvc.exception.NoSuchArticleException
import com.fastcampus.springmvc.model.Article
import com.fastcampus.springmvc.repository.ArticleRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
) {

    fun getById(
        id: Long,
    ) : Article{
        return articleRepository.findByIdOrNull(id) ?: throw NoSuchArticleException("Not Found article by id: $id")
    }

    fun getAll(
        title: String?,
    ) : List<Article> {
        return if (title.isNullOrEmpty()) {
            articleRepository.findAll()
        }
        else {
            articleRepository.findAllByTitleContains(title)
        }
    }

    @Transactional
    fun create(
        requestDto: ArticleCreateRequestDto,
    ): Article {
        return articleRepository.save(requestDto.toEntity())
    }

    @Transactional
    fun update(
        id: Long,
        requestDto: ArticleUpdateRequestDto,
    ): Article {
        val article = this.getById(id)

        article.let {
            requestDto.title?.let { article.title = it }
            requestDto.body?.let { article.body = it }
            requestDto.authorId?.let { article.authorId = it }
        }

        return article
    }

    @Transactional
    fun delete(
        id: Long
    ) {
        this.getById(id)
        articleRepository.deleteById(id)
    }
}