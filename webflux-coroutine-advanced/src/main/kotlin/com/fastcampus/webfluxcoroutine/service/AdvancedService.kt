package com.fastcampus.webfluxcoroutine.service

import com.fastcampus.webfluxcoroutine.repository.ArticleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

private val logger = KotlinLogging.logger {}

@Service
class AdvancedService(
    private val articleRepository: ArticleRepository,
) {

    suspend fun mdc() {
        logger.debug { "start mdc 1" }
        mdc2()
        logger.debug { "end mdc 1" }
    }

    suspend fun mdc2() {
        logger.debug { "start mdc 2" }
        delay(100)
        articleRepository.findById(1).let {
            logger.debug { "article: $it" }
        }
        Mono.fromCallable {
            logger.debug { "reactor call !!" }
        }.subscribeOn(Schedulers.boundedElastic()).awaitSingle()
        logger.debug { "end mdc 2" }
    }
}
