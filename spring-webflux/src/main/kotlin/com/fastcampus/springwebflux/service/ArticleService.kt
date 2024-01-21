package com.fastcampus.springwebflux.service

import com.fastcampus.springwebflux.controller.article.dto.ArticleCreateRequestDto
import com.fastcampus.springwebflux.controller.article.dto.ArticleCreateResponseDto
import com.fastcampus.springwebflux.controller.article.dto.ArticleUpdateRequestDto
import com.fastcampus.springwebflux.controller.article.dto.ArticleUpdateResponseDto
import com.fastcampus.springwebflux.controller.article.dto.toEntity
import com.fastcampus.springwebflux.exception.NotFoundException
import com.fastcampus.springwebflux.model.Article
import com.fastcampus.springwebflux.repository.ArticleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
@Transactional(readOnly = true)
class ArticleService(
    private val articleRepository: ArticleRepository,
) {

    fun get(id: Long): Mono<Article> {
        return articleRepository.findById(id)
            .switchIfEmpty { throw NotFoundException("article not found by id: $id") }
    }

    fun getAll(title: String? = null): Flux<Article> {
        return if (title.isNullOrEmpty()) articleRepository.findAll()
        else articleRepository.findAllByTitleContains(title)
    }

    @Transactional
    fun create(
        requestDto: ArticleCreateRequestDto,
    ): Mono<ArticleCreateResponseDto> {
        return articleRepository.save(requestDto.toEntity())
            .flatMap {
                if (it.title == "error") Mono.error(RuntimeException("error"))
                else Mono.just(it)
            }.map {
                ArticleCreateResponseDto(
                    id = it.id,
                    title = it.title ?: "empty title",
                    body = it.body,
                    authorId = it.authorId,
                    createdAt = it.createdAt,
                )
            }
    }

    @Transactional
    fun update(
        articleId: Long,
        requestDto: ArticleUpdateRequestDto,
    ): Mono<ArticleUpdateResponseDto> {
        return articleRepository.findById(articleId)
            .switchIfEmpty { throw NotFoundException("Not Found Article by Id $articleId") }
            .flatMap {
                articleRepository.save(
                    it.apply {
                        if (!requestDto.title.isNullOrEmpty()) it.title = requestDto.title
                        if (!requestDto.body.isNullOrEmpty()) it.body = requestDto.body
                        if (requestDto.authorId != null) it.authorId = requestDto.authorId
                    }
                )
            }.map {
                ArticleUpdateResponseDto(
                    title = it.title,
                    body = it.body,
                    authorId = it.authorId,
                    updatedAt = it.updatedAt,
                )
            }
    }

    @Transactional
    fun deleteById(id: Long): Mono<Void> {
        return articleRepository.deleteById(id)
    }

}