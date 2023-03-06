package com.codersee.webclientcoroutines

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebclientCoroutinesApplication

fun main(args: Array<String>) {
	runApplication<WebclientCoroutinesApplication>(*args)
}
