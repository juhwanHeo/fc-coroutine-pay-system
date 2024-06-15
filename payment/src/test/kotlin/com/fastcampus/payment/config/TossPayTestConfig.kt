package com.fastcampus.payment.config

import com.fastcampus.payment.service.TossPayApi
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile("toss-pay-test")
class TossPayTestConfig {

    @Bean
    @Primary
    fun testTossPayApi(): TossPayApi {
        return Mockito.mock(TossPayApi::class.java)
    }
}