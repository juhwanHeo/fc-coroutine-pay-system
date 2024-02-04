package com.fastcampus.webfluxcoroutine.repository

import com.fastcampus.webfluxcoroutine.model.Article
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class ArticleRepositoryTest(
    @Autowired private val articleRepository: ArticleRepository,
): StringSpec({
    "context load" {
        val saved = articleRepository.save(Article(title = "test"))

        saved.title shouldBe "test"
        articleRepository.delete(saved)
    }
})