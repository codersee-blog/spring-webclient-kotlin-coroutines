package com.codersee.webclientcoroutines.controller

import com.codersee.webclientcoroutines.service.UserApiService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userApiService: UserApiService
) {

    @GetMapping
    suspend fun findAllUsers() =
        userApiService.findAllUsersUsingExchange()

    @GetMapping("/{id}")
    suspend fun findUserById(
        @PathVariable id: Long
    ) =
        userApiService.findUserById(id)


    @DeleteMapping("/{id}")
    suspend fun deleteUserById(
        @PathVariable id: Long
    ) =
        userApiService.deleteUserById(id)

}