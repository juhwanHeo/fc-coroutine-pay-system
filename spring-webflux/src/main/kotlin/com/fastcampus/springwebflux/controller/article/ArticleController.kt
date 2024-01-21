package com.fastcampus.springwebflux.controller.article

import com.fastcampus.springwebflux.controller.article.dto.ArticleCreateRequestDto
import com.fastcampus.springwebflux.controller.article.dto.ArticleCreateResponseDto
import com.fastcampus.springwebflux.controller.article.dto.ArticleUpdateRequestDto
import com.fastcampus.springwebflux.controller.article.dto.ArticleUpdateResponseDto
import com.fastcampus.springwebflux.model.Article
import com.fastcampus.springwebflux.service.ArticleService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/article")
class ArticleController(
    private val articleService: ArticleService,
) {

    private val logger = KotlinLogging.logger {  }

    @PostMapping
    fun create(
        @RequestBody createRequestDto: ArticleCreateRequestDto,
    ): Mono<ArticleCreateResponseDto> {
        logger.debug { ">> createRequestDto: $createRequestDto"  }
        return articleService.create(createRequestDto)
    }

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
    ): Mono<Article> {
        return articleService.get(id)
    }

    @GetMapping
    fun getAll(
        @RequestParam title: String?,
    ): Flux<Article> {
        return articleService.getAll(title)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody updateRequestDto: ArticleUpdateRequestDto,
    ): Mono<ArticleUpdateResponseDto> {
        return articleService.update(id, updateRequestDto)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
    ): Mono<Void> {
        return articleService.deleteById(id)
    }
}
