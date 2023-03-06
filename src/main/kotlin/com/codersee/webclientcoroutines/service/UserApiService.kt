package com.codersee.webclientcoroutines.service

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*
import org.springframework.web.server.ResponseStatusException

@Service
class UserApiService(
    private val webClient: WebClient
) {

    private val logger = LoggerFactory.getLogger(UserApiService::class.java)

    // Simple awaitBody<>()
    suspend fun findAllUsers(): List<UserResponse> =
        webClient.get()
            .uri("/users")
            .retrieve()
            .awaitBody<List<UserResponse>>()

    suspend fun findUserById(id: Long): UserResponse? =
        webClient.get()
            .uri("/users/$id")
            .retrieve()
            .awaitBodyOrNull<UserResponse>()

    // Fetch as Flow
    fun findAllUsersFlow(): Flow<UserResponse> =
        webClient.get()
            .uri("/users")
            .retrieve()
            .bodyToFlow<UserResponse>()

    // since 5.3 due to the possibility to leak memory and/or connections;
    // please, use exchangeToMono(Function), exchangeToFlux(Function);
    // consider also using retrieve() which provides access to the response
    // status and headers via ResponseEntity along with error status handling.
    suspend fun findUserResponseEntityById(id: Long): ClientResponse? =
        webClient.get()
            .uri("/users/$id")
            .exchange()
            .awaitSingleOrNull()

    suspend fun findAllUsersUsingExchange(): List<UserResponse> =
        webClient.get()
            .uri("/users")
            .awaitExchange { clientResponse ->
                val headers = clientResponse.headers().asHttpHeaders()
                logger.info("Received response from users API. Headers: $headers")
                clientResponse.awaitBody<List<UserResponse>>()
            }

    // We can't use awaitExchange if we want to have the Flow - results in []
    fun findAllUsersFlowUsingExchange(): Flow<UserResponse> =
        webClient.get()
            .uri("/users")
            .exchangeToFlow { clientResponse ->
                val headers = clientResponse.headers().asHttpHeaders()
                logger.info("Received response from users API. Headers: $headers")
                clientResponse.bodyToFlow<UserResponse>()
            }

    // Handle 404 Not Found response
    suspend fun findUserByIdNotFoundHandling(id: Long): UserResponse =
        webClient.get()
            .uri("/users/$id")
            .retrieve()
            .onStatus({ responseStatus ->
                responseStatus == HttpStatus.NOT_FOUND
            }) { throw ResponseStatusException(HttpStatus.NOT_FOUND) }
            .awaitBody<UserResponse>()

    // Handle bodiless
    suspend fun deleteUserById(id: Long): Unit =
        webClient.delete()
            .uri("/users/$id")
            .retrieve()
            .awaitBody<Unit>()

    suspend fun deleteUserByIdUsingBodiless(id: Long): ResponseEntity<Void> =
        webClient.delete()
            .uri("/users/$id")
            .retrieve()
            .awaitBodilessEntity()
}

data class UserResponse(
    val id: Long,
    @JsonProperty("first_name") val firstName: String,
    @JsonProperty("last_name") val lastName: String
)