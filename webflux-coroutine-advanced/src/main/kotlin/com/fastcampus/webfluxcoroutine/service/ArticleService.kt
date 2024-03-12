package com.fastcampus.webfluxcoroutine.service

import com.fastcampus.webfluxcoroutine.config.extension.toLocalDate
import com.fastcampus.webfluxcoroutine.controller.article.dto.ArticleCreateRequestDto
import com.fastcampus.webfluxcoroutine.controller.article.dto.ArticleSearchReqDto
import com.fastcampus.webfluxcoroutine.controller.article.dto.ArticleUpdateRequestDto
import com.fastcampus.webfluxcoroutine.controller.article.dto.toEntity
import com.fastcampus.webfluxcoroutine.exception.NotFoundException
import com.fastcampus.webfluxcoroutine.model.Article
import com.fastcampus.webfluxcoroutine.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val dbClient: DatabaseClient,
) {

    suspend fun create(
        requestDto: ArticleCreateRequestDto
    ): Article {
        return articleRepository.save(requestDto.toEntity())
    }

    suspend fun get(
        id: Long,
    ): Article {
        return articleRepository.findById(id) ?: throw NotFoundException("not found article by id: $id")
    }

    suspend fun getAll(
        title: String? = null
    ): Flow<Article> {
        return if (title.isNullOrBlank()) {
            articleRepository.findAll()
        }
        else {
            articleRepository.findAllByTitleContains(title)
        }
    }

    suspend fun getAll(
        requestDto: ArticleSearchReqDto,
    ): Flow<Article> {
        val params = HashMap<String, Any>()
        var sql = dbClient.sql("""
            SELECT id, title, body, author_id, created_at, updated_at
            FROM TB_ARTICLE
            WHERE 1=1
            ${
                requestDto.title.query {
                    params["title"] = it.trim().let { "%$it%" }
                    "AND title LIKE :title"
                }
            }
            ${
                requestDto.authorId.query {
                    params["authorId"] = it
                    "AND author_id IN (:authorId)"
                }
            }
            ${
                requestDto.from.query {
                    params["from"] = it.toLocalDate()
                    "AND created_at >= :from"
                }
            }
            ${
                requestDto.to.query {
                    params["to"] = it.toLocalDate().plusDays(1)
                    "AND created_at <= :to"
                }
            }
        """.trimIndent())

        params.forEach { (key, value) -> sql = sql.bind(key, value) }
        return sql.map { row ->
            Article(
                id          = row.get("id") as Long,
                title       = row.get("title") as String,
                body        = row.get("body") as String?,
                authorId    =  row.get("author_id") as Long,
            ).apply {
                createdAt = row.get("created_at") as LocalDateTime?
                updatedAt = row.get("updated_at") as LocalDateTime?
            }
        }.flow()
    }

    suspend fun update(
        id: Long,
        requestDto: ArticleUpdateRequestDto,
    ): Article {
        val article = articleRepository.findById(id) ?: throw NotFoundException("not found article by id: $id")
        return articleRepository.save(article.apply {
            requestDto.title?.let { this.title = it }
            requestDto.body?.let { this.body = it }
            requestDto.authorId?.let { this.authorId = it }
        })
    }

    suspend fun delete(
        id: Long,
    ) {
        return articleRepository.deleteById(id)
    }
}


fun <T> T?.query(f: (T) -> String): String {
    return when {
        this == null -> ""
        this is String && this.isBlank() -> ""
        this is Collection<*> && this.isEmpty() -> ""
        this is Array<*> && this.isEmpty() -> ""
        else -> f.invoke(this)
    }
}