package com.fastcampus.springwebflux.service

import com.fastcampus.springwebflux.controller.article.dto.ArticleCreateRequestDto
import com.fastcampus.springwebflux.controller.article.dto.ArticleUpdateRequestDto
import com.fastcampus.springwebflux.exception.NotFoundException
import com.fastcampus.springwebflux.repository.ArticleRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ArticleServiceTest(
    @Autowired private val articleService: ArticleService,
    @Autowired private val articleRepository: ArticleRepository,
) {

    @AfterEach
    fun after() {
        articleRepository.deleteAll().block()
    }

    @Test
    fun createAndGet() {
        val saved = articleService.create(
            ArticleCreateRequestDto(
                title = "Title",
                body = "Body",
                authorId = 0L,
            )
        ).block()

        val find = saved?.let { articleService.get(it.id).block() }
        assertNotNull(saved?.id)
        assertEquals(saved?.id, find?.id)
        assertEquals(saved?.title, find?.title)
        assertEquals(saved?.body, find?.body)
        assertEquals(saved?.authorId, find?.authorId)
        assertNotNull(find?.createdAt)
        assertNotNull(find?.updatedAt)
    }

    @Test
    fun getAllTest() {
        articleService.create(ArticleCreateRequestDto(title = "Title 1", body = "Body 1", authorId = 0L)).block()
        articleService.create(ArticleCreateRequestDto(title = "Title 2", body = "Body 2", authorId = 0L)).block()
        articleService.create(ArticleCreateRequestDto(title = "Title test", body = "Body 3", authorId = 0L)).block()

        assertEquals(3, articleService.getAll().collectList().block()?.size)
        assertEquals(1, articleService.getAll("test").collectList().block()?.size)
    }

    @Test
    fun updateTest() {
        val saved = articleService.create(ArticleCreateRequestDto(title = "Title 1", body = "Body 1", authorId = 0L)).block()!!
        val updateRequestDto = ArticleUpdateRequestDto(title = "title update", body = "body update")
        articleService.update(saved.id, updateRequestDto).block()

        articleService.get(saved.id).block()!!.let {
            assertEquals(updateRequestDto.title, it.title)
            assertEquals(updateRequestDto.body, it.body)
            assertEquals(saved.authorId, it.authorId)
        }
    }


    @Test
    fun deleteTest() {
        val saved = articleService.create(ArticleCreateRequestDto(title = "Title 1", body = "Body 1", authorId = 0L)).block()!!
        articleService.deleteById(saved.id).block()

        assertThrows<NotFoundException> {
            articleService.get(saved.id).block()
        }
    }
}