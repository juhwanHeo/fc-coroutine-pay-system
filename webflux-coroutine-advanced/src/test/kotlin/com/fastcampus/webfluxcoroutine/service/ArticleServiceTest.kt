package com.fastcampus.webfluxcoroutine.service

import com.fastcampus.webfluxcoroutine.controller.article.dto.ArticleCreateRequestDto
import com.fastcampus.webfluxcoroutine.controller.article.dto.ArticleUpdateRequestDto
import com.fastcampus.webfluxcoroutine.exception.NotFoundException
import com.fastcampus.webfluxcoroutine.repository.ArticleRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@ActiveProfiles("test")
@SpringBootTest
class ArticleServiceTest(
    @Autowired private val articleService: ArticleService,
    @Autowired private val articleRepository: ArticleRepository,
    @Autowired private val rxtx: TransactionalOperator,
): StringSpec({

//    beforeTest {
//        articleRepository.deleteAll()
//    }

    "create and get" {
        rxtx.rollback {
            val requestDto = ArticleCreateRequestDto("test", null, null)
            val saved = articleService.create(requestDto)
            val get = articleService.get(saved.id)
            get.id shouldBe saved.id
            get.title shouldBe saved.title
            get.authorId shouldBe saved.authorId
            get.createdAt shouldNotBe null
            get.updatedAt shouldNotBe null
        }
    }

    "get all" {
        rxtx.rollback {
            articleService.create(ArticleCreateRequestDto("test1", null, null))
            articleService.create(ArticleCreateRequestDto("test2", null, null))
            articleService.create(ArticleCreateRequestDto("title matched 1", null, null))

            articleService.getAll().toList().size shouldBe 3
            articleService.getAll("matched").toList().size shouldBe 1
        }

    }

    "update" {
        rxtx.rollback {
            val saved = articleService.create(ArticleCreateRequestDto("test1", null, null))
            val updated = articleService.update(saved.id, ArticleUpdateRequestDto(null, "body Update"))

            saved.id shouldBe updated.id
            updated.body shouldBe "body Update"
        }
    }

    "delete" {
        rxtx.rollback {
            val saved = articleService.create(ArticleCreateRequestDto("test1", null, null))
            articleService.delete(saved.id)

            shouldThrow<NotFoundException> {
                articleService.get(saved.id)
            }
        }
    }
})

suspend fun <T> TransactionalOperator.rollback(f: suspend (ReactiveTransaction) -> T): T {
    return this.executeAndAwait { tx ->
        tx.setRollbackOnly()
        f.invoke(tx)
    }
}