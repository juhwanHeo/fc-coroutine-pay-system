package com.fastcampus.springwebflux.repository

import com.fastcampus.springwebflux.model.Article
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ArticleRepositoryTest(
    @Autowired private val articleRepository: ArticleRepository,
) {

    private val logger = KotlinLogging.logger {  }

    @Test
    fun test() {
        val saved = articleRepository.save(
            Article(
            title = "title",
            body = "body",
            authorId = 1234,
        )
        ).block()

        assertNotNull(saved)
        assertEquals("title", saved?.title)
        assertEquals("body", saved?.body)
        assertEquals(1234, saved?.authorId)
    }
}