package com.fastcampus.springmvc.controller.article

import com.fastcampus.springmvc.controller.article.dto.ArticleCreateRequestDto
import com.fastcampus.springmvc.controller.article.dto.ArticleUpdateRequestDto
import com.fastcampus.springmvc.model.Article
import com.fastcampus.springmvc.service.ArticleService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/article")
class ArticleController(
    private val articleService: ArticleService,
) {

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
    ): Article {
        return articleService.getById(id)
    }

    @GetMapping
    fun getAll(
        @RequestParam title: String?,
    ): List<Article> {
        return articleService.getAll(title)
    }

    @PostMapping
    fun create(
        @RequestBody requestDto: ArticleCreateRequestDto
    ): Article {
        return articleService.create(requestDto)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody requestDto: ArticleUpdateRequestDto,
    ): Article {
        return articleService.update(id, requestDto)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
    ) {
        return articleService.delete(id)
    }
}