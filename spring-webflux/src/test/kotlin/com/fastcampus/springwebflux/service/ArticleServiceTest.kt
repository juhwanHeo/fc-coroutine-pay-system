package com.fastcampus.springwebflux.service

import com.fastcampus.springwebflux.config.rollback
import com.fastcampus.springwebflux.controller.article.dto.ArticleCreateRequestDto
import com.fastcampus.springwebflux.controller.article.dto.ArticleUpdateRequestDto
import com.fastcampus.springwebflux.exception.NotFoundException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono

@SpringBootTest
class ArticleServiceTest(
    @Autowired private val articleService: ArticleService,
) {

    @Test
    fun createAndGet() {
        articleService.create(ArticleCreateRequestDto(title = "Title", body = "Body", authorId = 0L)).flatMap { saved ->
            articleService.get(saved.id).doOnNext { find ->
                assertNotNull(saved?.id)
                assertEquals(saved?.id, find?.id)
                assertEquals(saved?.title, find?.title)
                assertEquals(saved?.body, find?.body)
                assertEquals(saved?.authorId, find?.authorId)
                assertNotNull(find?.createdAt)
                assertNotNull(find?.updatedAt)
            }
        }.rollback().block()
    }

    @Test
    fun getAllTest() {
        Mono.zip(
            articleService.create(ArticleCreateRequestDto(title = "Title service 1", body = "Body 1", authorId = 0L)),
            articleService.create(ArticleCreateRequestDto(title = "Title service 2", body = "Body 2", authorId = 0L)),
            articleService.create(ArticleCreateRequestDto(title = "Title service getAllTest", body = "Body 3", authorId = 0L)),
        ).flatMap {
            articleService.getAll("service").collectList().doOnNext {
                assertEquals(3, it.size)
            }
        }.flatMap {
            articleService.getAll("getAllTest").collectList().doOnNext {
                assertEquals(1, it.size)
            }
        }.rollback().block()
    }

    @Test
    fun updateTest() {
        val updateRequestDto = ArticleUpdateRequestDto(title = "title update", body = "body update")
        articleService.create(ArticleCreateRequestDto(title = "Title 1", body = "Body 1", authorId = 0L)).flatMap { saved ->
            articleService.update(saved.id, updateRequestDto)
                .doOnNext { updated ->
                    assertEquals(updateRequestDto.title, updated.title)
                    assertEquals(updateRequestDto.body, updated.body)
                    assertEquals(saved.authorId, updated.authorId)
                }
        }.rollback().block()
    }

    @Test
    fun deleteTest() {
        articleService.create(ArticleCreateRequestDto(title = "Title 1", body = "Body 1", authorId = 0L)).flatMap { saved ->
            articleService.deleteById(saved.id).doOnNext {
                assertThrows<NotFoundException> {
                    articleService.get(saved.id).block()
                }
            }
        }.rollback().block()
    }
}
