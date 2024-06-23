package com.fastcampus.payment.advanced.service.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class PaymentApi(
    @Value("\${payment.self.domain}") private val domain: String,
) {

    private val client: WebClient = WebClient.builder()
        .baseUrl(domain)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()


    suspend fun recapture(orderId: Long) {
        client.put().uri("/order/recapture/${orderId}")
            .retrieve()
            .bodyToMono<String>()
            .subscribe()
    }
}