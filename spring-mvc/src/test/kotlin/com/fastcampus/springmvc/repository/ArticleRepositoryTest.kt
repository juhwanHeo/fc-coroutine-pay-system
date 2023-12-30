package com.fastcampus.springmvc.repository

import com.fastcampus.springmvc.model.Article
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
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
        val prev = articleRepository.count()
        articleRepository.save(Article(
            title = "title",
            body = "body",
            authorId = 1234,
        )).let { logger.debug { it } }
        val curr = articleRepository.count()
        logger.debug { ">> prev: $prev, curr: $curr" }
        assertEquals(prev + 1, curr)
    }
}