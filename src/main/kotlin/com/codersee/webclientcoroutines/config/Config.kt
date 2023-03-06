package com.codersee.webclientcoroutines.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class Config {

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl("http://localhost:8090")
            .build()

}