package com.fastcampus.webfluxcoroutine.controller.article

import com.fastcampus.webfluxcoroutine.controller.article.dto.ArticleCreateRequestDto
import com.fastcampus.webfluxcoroutine.controller.article.dto.ArticleSearchReqDto
import com.fastcampus.webfluxcoroutine.controller.article.dto.ArticleUpdateRequestDto
import com.fastcampus.webfluxcoroutine.model.Article
import com.fastcampus.webfluxcoroutine.service.ArticleService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/article")
class ArticleController(
    private val articleService: ArticleService
) {

    @GetMapping
    suspend fun getAll(
        @RequestParam title: String?,
    ): Flow<Article> {
        return articleService.getAll(title)
    }

    @GetMapping("/all")
    suspend fun getAll(
        searchReqDto: ArticleSearchReqDto,
    ): Flow<Article> {
        return articleService.getAllCached(searchReqDto)
    }

    @GetMapping("/{articleId}")
    suspend fun get(
        @PathVariable articleId: Long,
    ): Article {
        return articleService.get(articleId)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody request: ArticleCreateRequestDto): Article {
        return articleService.create(request)
    }

    @PutMapping("/{articleId}")
    suspend fun update(
        @PathVariable articleId: Long,
        @RequestBody request: ArticleUpdateRequestDto,
    ): Article {
        return articleService.update(articleId, request)
    }

    @DeleteMapping("/{articleId}")
    suspend fun delete(
        @PathVariable articleId: Long,
    ) {
        articleService.delete(articleId)
    }
}