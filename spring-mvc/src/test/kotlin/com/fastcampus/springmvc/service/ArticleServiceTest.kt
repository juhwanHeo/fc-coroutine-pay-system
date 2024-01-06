package com.fastcampus.springmvc.service

import com.fastcampus.springmvc.controller.article.dto.ArticleCreateRequestDto
import com.fastcampus.springmvc.controller.article.dto.ArticleUpdateRequestDto
import com.fastcampus.springmvc.model.Article
import com.fastcampus.springmvc.repository.ArticleRepository
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@SpringBootTest
@Transactional
class ArticleServiceTest(
    @Autowired private val articleService: ArticleService,
    @Autowired private val articleRepository: ArticleRepository,
) {

    @Test
    fun getAll() {

        val articleList = listOf(
            Article(title = "findAll title 1", body = "findAll body 1", authorId = 1L),
            Article(title = "findAll title 2", body = "findAll body 2", authorId = 2L),
            Article(title = "findAll title 3", body = "findAll body 3", authorId = 3L),
        )
        articleRepository.saveAll(articleList)

        assertEquals(articleList.size, articleService.getAll(null).size)
        assertEquals(1, articleService.getAll("2").size)
    }

    @Test
    fun getById() {
        val saved = articleRepository.save(Article(title = "findById title", body = "findById body", authorId = 1L))
        articleService.getById(saved.id).let {
            assertEquals("findById title", it.title)
            assertEquals("findById body", it.body)
            assertEquals(1L, it.authorId)
        }
    }

    @Test
    fun create() {
        val request = ArticleCreateRequestDto("title 4", "blabla 04", 1234)
        articleService.create(request).let {
            assertEquals(request.title, it.title)
            assertEquals(request.body, it.body)
            assertEquals(request.authorId, it.authorId)
        }
    }

    @Test
    fun update() {
        val newAuthorId = 999_999L
        val saved = articleRepository.save(Article(title = "update title", body = "update body", authorId = 999L))
        articleService.update(saved.id, ArticleUpdateRequestDto(authorId = newAuthorId)).let {
            assertEquals(newAuthorId, it.authorId)
        }
    }

    @Test
    fun delete() {
        val prevSize = articleService.getAll(null).size
        val new = articleService.create(ArticleCreateRequestDto("title 4", "blabla 04", 1234))
        assertEquals(prevSize + 1, articleService.getAll(null).size)
        articleService.delete(new.id)
        assertEquals(prevSize, articleService.getAll(null).size)
    }

}