package com.fastcampus.springwebflux.controller.article

import com.fastcampus.springwebflux.controller.article.dto.ArticleCreateRequestDto
import com.fastcampus.springwebflux.controller.article.dto.ArticleUpdateRequestDto
import com.fastcampus.springwebflux.exception.NotFoundException
import com.fastcampus.springwebflux.repository.ArticleRepository
import com.fastcampus.springwebflux.service.ArticleService
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.context.event.annotation.AfterTestClass
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
class ArticleControllerTest(
    @Autowired private val context: ApplicationContext,
    @Autowired private val articleService: ArticleService,
    @Autowired private val articleRepository: ArticleRepository,
) {

    val client = WebTestClient.bindToApplicationContext(context).build()
    val path = "/article"

    @AfterTestClass
    fun clean() {
        articleRepository.deleteAll()
    }

    @Nested
    inner class CreateTest {

        @Test
        fun createTest() {
            val createDto = ArticleCreateRequestDto(title = "title controller create test", body = "body controller create test", authorId = 1L)
            client.post().uri(path).accept(MediaType.APPLICATION_JSON).bodyValue(createDto).exchange()
                .expectStatus().isCreated
                .expectBody()
                .jsonPath("title").isEqualTo(createDto.title)
                .jsonPath("body").isEqualTo(createDto.body!!)
                .jsonPath("authorId").isEqualTo(createDto.authorId!!)
        }
    }

    @Nested
    inner class GetTest {

        @Test
        fun getTest() {
            val createDto = ArticleCreateRequestDto(title = "title controller get test", body = "body controller get test", authorId = 1L)
            val saved = articleService.create(createDto).block()!!

            client.get().uri("$path/${saved.id}").accept(MediaType.APPLICATION_JSON).exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("id").isEqualTo(saved.id)
                .jsonPath("title").isEqualTo(saved.title)
                .jsonPath("body").isEqualTo(saved.body!!)
                .jsonPath("authorId").isEqualTo(saved.authorId!!)
        }

        @Test
        fun getNotFoundTest() {
            client.get().uri("$path/-1").accept(MediaType.APPLICATION_JSON).exchange()
                .expectStatus().is4xxClientError
        }

        @Test
        fun getAllTest() {
            val createDto = ArticleCreateRequestDto(title = "title controller getAll test", body = "body controller getAll test", authorId = 1L)
            val saved = articleService.create(createDto).block()!!
            val findAll = articleService.getAll("").collectList().block()!!
            client.get().uri(path).accept(MediaType.APPLICATION_JSON).exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.length()").isEqualTo(findAll.size)
        }


        @Test
        fun getAllTitleTest() {
            val createDto = ArticleCreateRequestDto(title = "title controller getAllTitle test", body = "body controller getAllTitle test", authorId = 1L)
            val saved = articleService.create(createDto).block()!!
            client.get().uri("$path?title=getAllTitle").accept(MediaType.APPLICATION_JSON).exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
        }
    }

    @Nested
    inner class UpdateTest {

        @Test
        fun updateTest() {
            val createDto = ArticleCreateRequestDto(title = "title controller update test", body = "body controller update test", authorId = 1L)
            val saved = articleService.create(createDto).block()!!
            val requestDto = ArticleUpdateRequestDto(authorId = 9999L)

            client.put().uri("$path/${saved.id}").bodyValue(requestDto).accept(MediaType.APPLICATION_JSON).exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("authorId").isEqualTo(requestDto.authorId!!)
        }
    }

    @Nested
    inner class DeleteTest {

        @Test
        fun deleteTest() {
            val createDto = ArticleCreateRequestDto(title = "title controller delete test", body = "body controller delete test", authorId = 1L)
            val saved = articleService.create(createDto).block()!!

            client.delete().uri("$path/${saved.id}").accept(MediaType.APPLICATION_JSON).exchange()
                .expectStatus().isOk

            assertThrows<NotFoundException> {
                articleService.get(saved.id).block()
            }
        }
    }
}
