package com.fastcampus.webfluxcoroutine.controller.article

import com.fastcampus.webfluxcoroutine.controller.article.dto.ArticleCreateRequestDto
import com.fastcampus.webfluxcoroutine.model.Article
import io.kotest.core.spec.style.StringSpec
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType.*
import org.springframework.r2dbc.connection.init.ScriptUtils
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@ActiveProfiles("test")
@SpringBootTest
class ArticleControllerTest(
    @Autowired private val context: ApplicationContext,
    @Autowired private val client: DatabaseClient,
): StringSpec({

    beforeSpec {
        println(">> initialize db")
        val script = ClassPathResource("db-init/test.sql")
        client.inConnection { connection ->
            ScriptUtils.executeSqlScript(connection, script )
        }.block()
    }

    val client = WebTestClient.bindToApplicationContext(context).build()

    fun getArticleSize(): Int {
        val ref = object: ParameterizedTypeReference<ArrayList<Article>>(){}
        return client.get().uri("/article").accept(APPLICATION_JSON).exchange().expectBody(ref).returnResult().responseBody?.size ?: 0
    }

    "get all" {
        client.get().uri("/article").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(3)
        client.get().uri("/article?title=2").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
    }

    "get" {
        client.get().uri("/article/1").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk.expectBody()
            .jsonPath("title").isEqualTo("title 1")
            .jsonPath("body").isEqualTo("blabla 01")
            .jsonPath("authorId").isEqualTo("1234")
        client.get().uri("/article/-1").accept(APPLICATION_JSON).exchange()
            .expectStatus().is4xxClientError
    }

    "create" {
        val request = ArticleCreateRequestDto("test", "it is r2dbc demo", 1234)
        client.post().uri("/article").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("title").isEqualTo(request.title!!)
            .jsonPath("body").isEqualTo(request.body!!)
            .jsonPath("authorId").isEqualTo(request.authorId!!)
    }

    "update" {
        val request = ArticleCreateRequestDto(title = "test", body = "body", authorId = 999999)
        client.put().uri("/article/1").accept(APPLICATION_JSON).bodyValue(request).exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("authorId").isEqualTo(request.authorId!!)
    }

    "delete" {
        val prevSize = getArticleSize()
        val res = client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(ArticleCreateRequestDto("test", "it is r2dbc demo", 1234)).exchange()
            .expectBody(Article::class.java).returnResult().responseBody!!
        assertEquals(prevSize + 1, getArticleSize())
        client.delete().uri("/article/${res.id}").exchange().expectStatus().isOk
        assertEquals(prevSize, getArticleSize())
    }

})